package com.webflix.comments.services.beans;

import com.webflix.comments.models.entities.CommentEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.List;

@RequestScoped
public class CommentDataBean {

	@PersistenceContext(unitName = "webflix-jpa")
	private EntityManager em;

	private Client httpClient;
	private String baseUsersUrl;
	private String baseUrl;

	@PostConstruct
	private void init() {
		httpClient = ClientBuilder.newClient();
		baseUsersUrl = "users:8080"; // "http://martin.zoxxnet.com"; // "http://localhost:8090";
		baseUrl = "http://localhost:8090";
	}

	public String manageUser(String idTokenString) {
		HttpResponse userAuthResponse = null;

		try {
			HttpClient client = HttpClients.custom().build();
			HttpUriRequest request = RequestBuilder.get()
					.setUri(baseUsersUrl + "/users/v1/user") //.setUri(baseUrl + "/v1/user")
					.setHeader("ID-Token", idTokenString)
					.build();
			userAuthResponse = client.execute(request);

		} catch (Exception e) {
			System.out.println(e);
		}

		if (userAuthResponse != null && userAuthResponse.getStatusLine().getStatusCode() == 200) {

			try {
				HttpEntity entity = userAuthResponse.getEntity();
				String userId = EntityUtils.toString(entity);
				System.out.println("User ID: " + userId);

				return userId;
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return null;
	}

	public List<CommentEntity> getCommentsData(Integer videoId) {

		return em.createQuery(
				"SELECT c FROM CommentEntity c WHERE c.video_id = :videoId", CommentEntity.class)
				.setParameter("videoId", videoId)
				.getResultList();
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
