package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.selectDisscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDisscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDisscussPostById(243));
    }

    @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussPostMapper.selectDisscussPosts(101, 0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDisscussPosts(102, 0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDisscussPosts(103, 0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDisscussPosts(111, 0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDisscussPosts(112, 0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDisscussPosts(131, 0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDisscussPosts(132, 0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDisscussPosts(133, 0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDisscussPosts(134, 0,100));

    }

    @Test
    public void testUpdate(){
        DiscussPost post = discussPostMapper.selectDisscussPostById(231);
        post.setContent("1111111");
        discussPostRepository.save(post);
    }

    //搜索功能，内容不整合高亮
    @Test
    public void testSearchByRepository(){
        Pageable pageable = PageRequest.of(0, 10);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(pageable)
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for(DiscussPost post : page){
            System.out.println(post);
        }

    }


    //搜索功能，内容整合高亮，用template
    @Test
    public void testSearchByTemplate(){
        Pageable pageable = PageRequest.of(0, 10);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(pageable)
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> searchHits = restTemplate.search(searchQuery, DiscussPost.class);
        if(searchHits.getTotalHits() <= 0) {
            return;
        }

        List<DiscussPost> list = new ArrayList<>();
        for(SearchHit<DiscussPost> hit : searchHits){
            DiscussPost content = hit.getContent();
            DiscussPost post = new DiscussPost();
            BeanUtils.copyProperties(content, post);

            //处理高亮显示的结果
            List<String> list1 = hit.getHighlightFields().get("title");
            if(list1 != null){
                post.setTitle(list1.get(0));
            }

            List<String> list2 = hit.getHighlightFields().get("content");
            if(list2 != null){
                post.setContent(list2.get(0));
            }

            list.add(post);
        }

        Page<DiscussPost> page = new PageImpl<>(list, pageable, searchHits.getTotalHits());


        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for(DiscussPost post : page){
            System.out.println(post);
        }


    }






}
