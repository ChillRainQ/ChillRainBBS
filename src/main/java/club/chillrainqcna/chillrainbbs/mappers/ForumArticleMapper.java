package club.chillrainqcna.chillrainbbs.mappers;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumArticleMapper extends BaseMapper<ForumArticle> {
    Integer updateArticleCountMessage(@Param("type") Integer type, @Param("count") Integer count, @Param("articleId") String articleId);
    void updateBoardNameBatch(@Param("boardType") Integer boardType,
                              @Param("boardName") String boardName,
                              @Param("boardId") Integer boardId);
}
