package club.chillrainqcna.chillrainbbs.entity.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 
 * 文件信息
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForumArticleAttachment implements Serializable {


	/**
	 * 文件ID
	 */
	private String fileId;

	/**
	 * 文章ID
	 */
	private String articleId;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 下载次数
	 */
	private Integer downloadCount;

	/**
	 * 文件路径
	 */
	private String filePath;

	/**
	 * 文件类型
	 */
	private Integer fileType;

	/**
	 * 下载所需积分
	 */
	private Integer integral;


}
