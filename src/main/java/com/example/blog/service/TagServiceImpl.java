package com.example.blog.service;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.blog.NotFoundException;
import com.example.blog.dao.TagRepository;
import com.example.blog.po.Tag;

@Service
public class TagServiceImpl implements TagService {

	@Autowired
	private TagRepository tagRepository;

	@Transactional
	@Override
	public Tag saveTag(Tag tag) {
		return tagRepository.save(tag);
	}

	@Transactional
	@Override
	public Tag getTag(Long id) {
		return tagRepository.findById(id).orElse(null);
	}
	
	@Override
	public Tag getTagByName(String name) {
		return tagRepository.findByName(name);
	}

	@Transactional
	@Override
	// Page => 分頁查詢
	public Page<Tag> listTag(Pageable pageable) {
		return tagRepository.findAll(pageable);
	}

	@Transactional
	@Override
	public Tag updateTag(Long id, Tag tag) {
		Tag tempTag = tagRepository.findById(id).orElseThrow(() -> new NotFoundException("不存在該類型"));
		BeanUtils.copyProperties(tag, tempTag);
		return tagRepository.save(tempTag);
	}

	@Transactional
	@Override
	public void deleteTag(Long id) {
		tagRepository.deleteById(id);

	}

}
