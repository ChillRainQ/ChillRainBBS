package club.chillrainqcna.chillrainbbs.entity.bean;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * 文章板块信息
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ForumBoard implements Serializable {


	/**
	 * 板块ID
	 */
	private Integer boardId;

	/**
	 * 父级板块ID
	 */
	private Integer pBoardId;

	/**
	 * 板块名
	 */
	private String boardName;

	/**
	 * 封面
	 */
	private String cover;

	/**
	 * 描述
	 */
	private String boardDesc;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 0:只允许管理员发帖 1:任何人可以发帖
	 */
	private Integer postType;

	@TableField(exist = false)
	private List<ForumBoard> children;



}
