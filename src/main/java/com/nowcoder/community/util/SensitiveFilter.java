package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //替换字符
    private static final String REPLACEMENT = "**";

    //初始化前缀树
    private TrieNode rootNode = new TrieNode();


    //初始化方法，容器实例化后方法自动调用（服务启动后调用）
    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){

            String keyword;
            while((keyword = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyWord(keyword);

            }

        }catch (IOException e){
            logger.error("加载敏感词文件失败", e);
        }


    }

    //将敏感词添加到前缀树中
    private void addKeyWord(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            tempNode = subNode;

            //设置结束标识
            if(i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }

        }

    }

    //过滤敏感词生成字符串
    //传入原文本text return过滤后的文本
    public String filter(String text){
        //text为空
        if(StringUtils.isBlank(text)){
            return null;
        }

        //前缀树指针
        TrieNode tempNode = rootNode;
        //指针1
        int begin = 0;
        //指针2
        int position = 0;
        //返回的结果
        StringBuilder sb = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);

            //跳过符号
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                //begin开头的词不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;

                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                //begin~position的是敏感词,替换
                sb.append(REPLACEMENT);
                begin = ++position;

                tempNode = rootNode;
            } else{
                position++;
            }

        }
        //position到头后begin还没有到头，添加最后一个字符串
        sb.append(text.substring(begin));
        return sb.toString();

    }


    //辅助函数：判断是否为符号
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }









    //前缀树
    private class TrieNode{
        private boolean isKeywordEnd = false;

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }


        //子节点(key是该字符，value是下级字符)
        private Map<Character, TrieNode> subNodes = new HashMap<>();
        //添加子节点方法
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }
        //获取子节点方法
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

}
