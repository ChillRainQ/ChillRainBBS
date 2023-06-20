package club.chillrainqcna.chillrainbbs.mappers;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumArticleAttachmentDownload;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumArticleAttachmentDownloadMapper extends BaseMapper<ForumArticleAttachmentDownload> {
    Integer insertOrUpdate(ForumArticleAttachmentDownload record);
}
