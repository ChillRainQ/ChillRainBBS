package club.chillrainqcna.chillrainbbs.entity.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 
 * 用户附件下载
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForumArticleAttachmentDownload implements Serializable {


	/**
	 * 文件ID
	 */
	private String fileId;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 文章ID
	 */
	private String articleId;

	/**
	 * 文件下载次数
	 */
	private Integer downloadCount;
}
