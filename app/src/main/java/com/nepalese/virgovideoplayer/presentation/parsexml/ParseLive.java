package com.nepalese.virgovideoplayer.presentation.parsexml;/*
 *  Nepalese created on 2020/11/22
 * usage:
 */

import com.nepalese.virgovideoplayer.data.bean.LiveSource;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ParseLive extends DefaultHandler{

    public static final String ELEMENT_LIVE = "Live";
    private List<LiveSource> mList;
    private LiveSource mSource;

    public ParseLive() {
    }

    public List<LiveSource> parse(File file, String encodeType) {
        if (file != null && file.exists()) {
            FileInputStream input;

            try {
                input = new FileInputStream(file);
                InputSource inputSource = new InputSource(new InputStreamReader(input, encodeType));
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser parser = spf.newSAXParser();
                parser.parse(inputSource, this);
                input.close();
            } catch (Throwable var7) {
                var7.printStackTrace();
            }

            return this.mList;
        } else {
            System.out.println("文件不存在！");
            return null;
        }
    }

    public void startDocument() {
        this.mList = new ArrayList();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (ELEMENT_LIVE.equals(qName)) {
            mSource = new LiveSource();

            if (attributes != null) {
                int size = attributes.getLength();
                for (int i = 0; i < size; i++) {
                    mSource.setValue(attributes.getQName(i), attributes.getValue(i));
                }
            }
        }
    }

    public void characters(char[] ch, int start, int length) {
    }

    public void endElement(String uri, String localName, String qName) {
        // 按元素尾 (尾标签) 标识解析完一条元素 插入到 list
        if (ELEMENT_LIVE.equals(qName)) {
            mList.add(mSource);
        }
    }
}
