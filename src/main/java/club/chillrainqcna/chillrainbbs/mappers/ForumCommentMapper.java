package club.chillrainqcna.chillrainbbs.mappers;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumArticle;
import club.chillrainqcna.chillrainbbs.entity.bean.ForumComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumCommentMapper extends BaseMapper<ForumComment> {
//    Integer updateCommentDoLike(Integer commentId, Integer count);
}
