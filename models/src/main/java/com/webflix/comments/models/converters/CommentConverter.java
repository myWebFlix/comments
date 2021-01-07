package com.webflix.comments.models.converters;

import com.webflix.comments.models.dtos.Comment;
import com.webflix.comments.models.entities.CommentEntity;

public class CommentConverter {

	public static Comment toDto(CommentEntity ce) {
		Comment c = new Comment();
		c.setText(ce.getComment_text());
		c.setTimestamp(ce.getComment_timestamp());

		return c;
	}

	public static CommentEntity toEntity(Comment c) {
		CommentEntity ce = new CommentEntity();
		ce.setComment_text(c.getText());
		ce.setComment_timestamp(c.getTimestamp());

		return ce;
	}

}
