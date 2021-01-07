package com.webflix.comments.services.beans;

import com.webflix.comments.models.converters.CommentConverter;
import com.webflix.comments.models.dtos.Comment;
import com.webflix.comments.models.entities.CommentEntity;
import com.webflix.comments.services.config.RestConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class CommentDataBean {

	@Inject
	private RestConfig restConfig;

	@Inject
	private CommentDataBean commentDataBeanProxy;

	@PersistenceContext(unitName = "webflix-jpa")
	private EntityManager em;

	private HttpClient httpClient;

	@PostConstruct
	private void init() {
		httpClient = HttpClientBuilder.create().build();
	}

	public Integer manageUser(String idTokenString) {
		HttpResponse userAuthResponse = null;

		try {
			HttpClient client = HttpClients.custom().build();
			HttpUriRequest request = RequestBuilder.get()
					.setUri("http://users:8080/v1/auth")
					.setHeader("ID-Token", idTokenString)
					.build();
			userAuthResponse = client.execute(request);

		} catch (Exception e) {
			System.out.println(e);
		}

		if (userAuthResponse != null && userAuthResponse.getStatusLine().getStatusCode() == 200) {

			try {
				HttpEntity entity = userAuthResponse.getEntity();
				Integer userId = Integer.parseInt(EntityUtils.toString(entity));
				System.out.println("User ID: " + userId);

				return userId;
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return null;
	}

	public List<Comment> getComments(Integer videoId) {
		return em.createQuery(
				"SELECT c FROM CommentEntity c WHERE c.video_id = :videoId", CommentEntity.class)
				.setParameter("videoId", videoId)
				.getResultList()
				.stream().map(entity -> {
					Comment comment = CommentConverter.toDto(entity);
					comment.setUser_name(commentDataBeanProxy.getUserName(entity.getComment_user_id()));
					return comment;
				}).collect(Collectors.toList());
	}

	@Retry
	@Timeout(value = 2, unit = ChronoUnit.SECONDS)
	@CircuitBreaker(requestVolumeThreshold = 3)
	@Fallback(fallbackMethod = "getUserNameFallback")
	public String getUserName(Integer userId) {
		try {
			String url = "http://users:8080/v1/users/" + userId;
			if (restConfig.isDisableUserInfo()) {
				url = "http://users:8081/v1/users/" + userId;
			}

			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);

			String json = EntityUtils.toString(response.getEntity());
			JSONObject obj = new JSONObject(json);

			return obj.getString("user_name");

		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}

	public String getUserNameFallback(Integer userId) {
		return "Unknown User";
	}

	public CommentEntity createComment(CommentEntity comment) {

		try {
			beginTx();
			em.persist(comment);
			commitTx();
		} catch (Exception e) {
			rollbackTx();
		}

		if (comment.getId() == null) {
			throw new RuntimeException("Entity was not persisted");
		}

		return comment;
	}

	public boolean deleteComment(Integer commentId) {

		CommentEntity comment = em.find(CommentEntity.class, commentId);

		if (comment != null) {
			try {
				beginTx();
				em.remove(comment);
				commitTx();
			} catch (Exception e) {
				rollbackTx();
			}
		} else
			return false;

		return true;
	}

	// Transactions

	private void beginTx() {
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
	}

	private void commitTx() {
		if (em.getTransaction().isActive())
			em.getTransaction().commit();
	}

	private void rollbackTx() {
		if (em.getTransaction().isActive())
			em.getTransaction().rollback();
	}

}
