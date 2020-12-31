package com.webflix.comments.api.v1.resources;

import com.webflix.comments.models.entities.CommentEntity;

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
public class CommentsResource {

	@Inject
	private com.webflix.comments.services.beans.CommentDataBean commentDataBean;

	@GET
	@Path("/{idVideo}")
	public Response getUser(@HeaderParam("ID-Token") String idTokenString,
							@PathParam("idVideo") Integer idVideo) {
		String userId = commentDataBean.manageUser(idTokenString);

		if (userId != null) {

			List<CommentEntity> commentsForVideo = commentDataBean.getCommentsData(idVideo);

			return Response.ok(commentsForVideo).build();

		} else {

			return Response.status(Response.Status.UNAUTHORIZED).build();

		}
	}

	@POST
	@Path("/{idVideo}")
	public Response createVideoMetadata(@HeaderParam("ID-Token") String idTokenString,
										CommentEntity comment,
										@PathParam("idVideo") Integer idVideo) {

		String userId = commentDataBean.manageUser(idTokenString);

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

	@DELETE
	@Path("{commentId}")
	public Response deleteVideoMetadata(@HeaderParam("ID-Token") String idTokenString,
										@PathParam("commentId") Integer commentId) {

		String userId = commentDataBean.manageUser(idTokenString);

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
