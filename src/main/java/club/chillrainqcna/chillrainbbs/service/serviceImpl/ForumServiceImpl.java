package club.chillrainqcna.chillrainbbs.service.serviceImpl;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumBoard;
import club.chillrainqcna.chillrainbbs.exception.ChillRainBBSException;
import club.chillrainqcna.chillrainbbs.mappers.ForumArticleMapper;
import club.chillrainqcna.chillrainbbs.mappers.ForumMapper;
import club.chillrainqcna.chillrainbbs.service.ForumService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChillRain 2023 04 19
 */
@Service
public class ForumServiceImpl implements ForumService {
    @Resource
    private ForumArticleMapper forumArticleMapper;
    @Resource
    private ForumMapper forumMapper;

    @Override
    public List<ForumBoard> getForumBoard(Integer postType) {
        List<ForumBoard> list = null;
        QueryWrapper query = new QueryWrapper();
        if(postType != null){
            query.eq("post_type", postType);
            list = forumMapper.selectList(query);
        }else{
            list = forumMapper.selectList(query);
        }
        return line2Tree(list, 0);
    }

    @Override
    public void saveForumBoard(ForumBoard board) {
        if(board.getBoardId() == null){//新增
            QueryWrapper<ForumBoard> query = new QueryWrapper<>();
            query.eq("p_board_id", board.getPBoardId());
            ForumBoard forumBoard = forumMapper.selectOne(query);
            board.setSort(forumBoard.getSort() + 1);
            forumMapper.insert(board);
        }else {//修改
            ForumBoard dbInfo = forumMapper.selectOne(new QueryWrapper<ForumBoard>().eq("board_id", board.getBoardId()));
            if(dbInfo == null){//数据库中没有
                throw new ChillRainBBSException("版块信息不存在");
            }
            forumMapper.updateById(board);
            if(!dbInfo.getBoardName().equals(board.getBoardName())){//名称不一样 修改名称后 修改所有文章的板块名
                forumArticleMapper.updateBoardNameBatch(dbInfo.getPBoardId() == 0 ? 0 : 1, board.getBoardName(), board.getBoardId());
            }
        }
    }

    @Override
    public void changeSort(String boardIds) {
        String[] ids = boardIds.split(",");
        Integer index = 1;
        for (String id : ids) {
            Integer boardId = Integer.parseInt(id);
            ForumBoard forumBoard = new ForumBoard();
            forumBoard.setSort(boardId);
            forumMapper.update(forumBoard, new QueryWrapper<ForumBoard>().eq("board_id", boardId));
            index ++;
        }
    }

    /**
     * 构建树形结构
     * @param dataList
     * @param pid
     * @return
     */
    private List<ForumBoard> line2Tree(List<ForumBoard> dataList, Integer pid){
        ArrayList<ForumBoard> children = new ArrayList<>();
        for (ForumBoard child : dataList) {
            if(child.getPBoardId().equals(pid)){
                child.setChildren(line2Tree(dataList, child.getBoardId()));
                children.add(child);
            }
        }
        return children;
    }
}
