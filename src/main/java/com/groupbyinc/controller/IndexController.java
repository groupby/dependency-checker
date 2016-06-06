package com.groupbyinc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    private static final transient Logger LOG = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/")
    public String home(Model model) {
      return "index";
    }

}