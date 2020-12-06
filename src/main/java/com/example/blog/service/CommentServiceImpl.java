package com.example.blog.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.blog.ErrorException;
import com.example.blog.dao.CommentRepository;
import com.example.blog.po.Comment;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	CommentRepository commentRepository;

	@Override
	public List<Comment> listCommentByBlogId(Long blogId) {
		List<Comment> comments = commentRepository.findByBlogIdAndParentCommentNull(blogId, Sort.by("createTime"));
		return eachComment(comments) ;
	}

	@Transactional
	@Override
	public Comment savaComment(Comment comment) {
		Long parentCommentId = comment.getParentComment().getId();

		if (parentCommentId != -1) {
			comment.setParentComment(
					commentRepository.findById(parentCommentId).orElseThrow(() -> new ErrorException("找不到留言")));
		} else {
			comment.setParentComment(null);
		}

		comment.setCreateTime(new Date());

		return commentRepository.save(comment);
	}

    private List<Comment> eachComment(List<Comment> comments) {
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties(comment,c);
            commentsView.add(c);
        }
        
        combineChildren(commentsView);
        return commentsView;
    }

    private void combineChildren(List<Comment> comments) {

        for (Comment comment : comments) {
            List<Comment> replys1 = comment.getReplyComments();
            for(Comment reply1 : replys1) {
                recursively(reply1);
            }
            comment.setReplyComments(tempReplys);
            tempReplys = new ArrayList<>();
        }
    }

    private List<Comment> tempReplys = new ArrayList<>();
    
    private void recursively(Comment comment) {
        tempReplys.add(comment);
        if (comment.getReplyComments().size()>0) {
            List<Comment> replys = comment.getReplyComments();
            for (Comment reply : replys) {
                tempReplys.add(reply);
                if (reply.getReplyComments().size()>0) {
                    recursively(reply);
                }
            }
        }
    }
}
