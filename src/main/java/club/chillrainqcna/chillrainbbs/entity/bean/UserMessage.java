package club.chillrainqcna.chillrainbbs.entity.bean;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 用户消息
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserMessage implements Serializable {


	/**
	 * 自增ID
	 */
	private Integer messageId;

	/**
	 * 接收人用户ID
	 */
	private String receivedUserId;

	/**
	 * 文章ID
	 */
	private String articleId;

	/**
	 * 文章标题
	 */
	private String articleTitle;

	/**
	 * 评论ID
	 */
	private Integer commentId;

	/**
	 * 发送人用户ID
	 */
	private String sendUserId;

	/**
	 * 发送人昵称
	 */
	private String sendNickName;

	/**
	 * 0:系统消息 1:评论 2:文章点赞  3:评论点赞 4:附件下载
	 */
	private Integer messageType;

	/**
	 * 消息内容
	 */
	private String messageContent;

	/**
	 * 1:未读 2:已读
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;





}
