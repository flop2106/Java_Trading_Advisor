package com.project1.trading_automation.model;

import com.project1.trading_automation.utils.NewsAPIHandler;
import java.util.*;

public class News {
    private String title;
    private String url;
    private String content;

    public News(String title, String url, String content){
        this.title = title;
        this.url = url;
        this.content = content;
    }

    // Getters
    public String getTitle(){return title;}
    public String getUrl(){return url;}
    public String getContent(){return content;}

    public String summarize(){
        return NewsAPIHandler.summarizeNews(content);
    }

    public static List<News> fetchNews(String query, int maxResults){
        NewsAPIHandler apiHandler = new NewsAPIHandler();
        List<Map<String, String>> rawNewsList = apiHandler.getNews(query, maxResults);

        List<News> newsList = new ArrayList<>();
        for (Map<String, String> rawNews : rawNewsList){
            newsList.add(new News(
                rawNews.get("title"), 
                rawNews.get("url"),
                rawNews.get("content")
            ));
        }

        return newsList;
    }

    @Override
    public String toString(){
        return "Title: " + title + "\nURL: " + url + "\nContent: " + summarize() + "\n";
    }
}
