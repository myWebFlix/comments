package com.webflix.comments.graphql;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.utils.GraphQLUtils;
import com.webflix.comments.models.dtos.Comment;
import com.webflix.comments.services.beans.CommentDataBean;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@GraphQLClass
@ApplicationScoped
public class CommentQueries {

	@Inject
	private CommentDataBean commentDataBean;

	@GraphQLQuery
	public PaginationWrapper<Comment> allComments(
			@GraphQLArgument(name = "pagination") Pagination pagination,
			@GraphQLArgument(name = "video_id") Integer videoId) {
		return GraphQLUtils.process(commentDataBean.getComments(videoId), pagination);
	}

}
