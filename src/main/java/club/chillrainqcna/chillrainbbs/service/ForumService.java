package club.chillrainqcna.chillrainbbs.service;

import club.chillrainqcna.chillrainbbs.entity.bean.ForumBoard;

import java.util.List;

public interface ForumService {
    List<ForumBoard> getForumBoard(Integer postType);

    void saveForumBoard(ForumBoard board);

    void changeSort(String boardIds);
}
