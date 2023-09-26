package com.lml.community.community.controller;

import com.lml.community.community.entity.Comment;
import com.lml.community.community.entity.DiscussPost;
import com.lml.community.community.entity.Page;
import com.lml.community.community.entity.User;
import com.lml.community.community.service.CommentService;
import com.lml.community.community.service.DiscussPostService;
import com.lml.community.community.service.LikeService;
import com.lml.community.community.service.UserService;
import com.lml.community.community.util.CommunityConstant;
import com.lml.community.community.util.CommunityUtil;
import com.lml.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(403, "你还没有登录哦！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDicussPost(post);

        // 报错的情况将来统一处理
        return CommunityUtil.getJsonString(0, "发布成功！");
    }

    @RequestMapping(path = "/detail/{disscussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("disscussPostId") int disscussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDisscussPostById(disscussPostId);
        model.addAttribute("post", post);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, disscussPostId);
        model.addAttribute("likeCount", likeCount);
        // 点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, disscussPostId);
        model.addAttribute("likeStatus", likeStatus);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + disscussPostId);
        page.setRows(post.getCommentCount());

        // 评论： 给帖子的评论
        // 回复： 给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST,
                post.getId(), page.getOffset(), page.getLimit());
        // 评论view列表
        List<Map<String, Object>> commentViewList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment: commentList) {
                // 评论view
                Map<String, Object> commentView = new HashMap<>();
                // 评论
                commentView.put("comment", comment);
                // 作者
                commentView.put("user", userService.findUserById(comment.getUserId()));
                // 点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentView.put("likeCount", likeCount);
                // 点赞状态
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentView.put("likeStatus", likeStatus);

                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,
                        comment.getId(), 0, Integer.MAX_VALUE);
                // 回复view列表
                List<Map<String, Object>> replyViewList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply: replyList) {
                        Map<String, Object> replyView = new HashMap<>();
                        // 回复
                        replyView.put("reply", reply);
                        // 作者
                        replyView.put("user", userService.findUserById(reply.getUserId()));
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyView.put("target", target);
                        // 点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyView.put("likeCount", likeCount);
                        // 点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyView.put("likeStatus", likeStatus);
                        replyViewList.add(replyView);
                    }
                }
                commentView.put("replies", replyViewList);
                // 回复数量
                int replyCount = commentService.findCommentsCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentView.put("replyCount", replyCount);

                commentViewList.add(commentView);
            }
        }

        model.addAttribute("comments", commentViewList);

        return "/site/discuss-detail";
    }
}
