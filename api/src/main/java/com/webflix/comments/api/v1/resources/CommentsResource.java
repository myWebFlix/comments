package com.webflix.comments.api.v1.resources;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import com.webflix.comments.models.dtos.Comment;
import com.webflix.comments.models.entities.CommentEntity;
import com.webflix.comments.services.config.RestConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@ApplicationScoped
@Path("/comments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
// DO NOT UNCOMMENT // @CrossOrigin(supportedMethods = "GET, POST, HEAD, DELETE, OPTIONS")
public class CommentsResource {

	@Inject
	private com.webflix.comments.services.beans.CommentDataBean commentDataBean;

	@Inject
	private RestConfig restConfig;

	@GET
	@Path("/{idVideo}")
	public Response getComments(@HeaderParam("ID-Token") String idTokenString,
							@PathParam("idVideo") Integer idVideo) {
		Integer userId = commentDataBean.manageUser(idTokenString);

		if (userId != null) {

			List<Comment> commentsForVideo = commentDataBean.getComments(idVideo);

			return Response.ok(commentsForVideo).build();

		} else {

			return Response.status(Response.Status.UNAUTHORIZED).build();

		}
	}

	@POST
	@Path("/{idVideo}")
	public Response postComment(@HeaderParam("ID-Token") String idTokenString,
										CommentEntity comment,
										@PathParam("idVideo") Integer idVideo) {

		Integer userId = commentDataBean.manageUser(idTokenString);

		if (restConfig.isDisableCommentsSubmit()) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		} else {
			if (userId != null) {

				if (comment.getComment_text().length() <= 0) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				} else {
					comment.setComment_user_id(userId);
					comment.setComment_timestamp(new Date());
					comment.setVideo_id(idVideo);
					comment = commentDataBean.createComment(comment);
				}

				return Response.status(Response.Status.OK).entity(comment).build();

			} else {

				return Response.status(Response.Status.UNAUTHORIZED).build();

			}
		}
	}

	@DELETE
	@Path("{commentId}")
	public Response deleteComment(@HeaderParam("ID-Token") String idTokenString,
										@PathParam("commentId") Integer commentId) {

		Integer userId = commentDataBean.manageUser(idTokenString);

		if (userId != null) {

			boolean deleted = commentDataBean.deleteComment(commentId);

			if (deleted) {
				return Response.status(Response.Status.NO_CONTENT).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

		} else {

			return Response.status(Response.Status.UNAUTHORIZED).build();

		}
	}

}
