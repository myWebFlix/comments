package com.webflix.comments.models.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comments_data")
@NamedQueries(value =
		{
				@NamedQuery(name = "CommentEntity.getAll",
						query = "SELECT c FROM CommentEntity c")
		})
public class CommentEntity {

	// Fields

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "video_id")
	private Integer video_id;

	@Column(name = "comment_user_id")
	private Integer comment_user_id;

	@Column(name = "comment_timestamp")
	private Date comment_timestamp;

	@Column(name = "comment_text")
	private String comment_text;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVideo_id() {
		return video_id;
	}

	public void setVideo_id(Integer video_id) {
		this.video_id = video_id;
	}

	public Integer getComment_user_id() {
		return comment_user_id;
	}

	public void setComment_user_id(Integer comment_user_id) {
		this.comment_user_id = comment_user_id;
	}

	public Date getComment_timestamp() {
		return comment_timestamp;
	}

	public void setComment_timestamp(Date comment_timestamp) {
		this.comment_timestamp = comment_timestamp;
	}

	public String getComment_text() {
		return comment_text;
	}

	public void setComment_text(String comment_text) {
		this.comment_text = comment_text;
	}
}
