package club.chillrainqcna.chillrainbbs.mappers;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumArticleAttachment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface ForumArticleAttachmentMapper extends BaseMapper<ForumArticleAttachment> {
    Integer updateDownloadCount(@Param("fileId") String fileId);
}
