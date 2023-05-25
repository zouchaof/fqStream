package com.register.server.web.controller;

import com.register.server.core.RegisterAgentFactory;
import com.register.server.web.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("rv-manage")
public class ManageController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/home")
    public String index(Model model) {
//        ModelAndView mv = new ModelAndView("hello");
//        mv.addObject("title", "Hello");
//        mv.addObject("message", "Hello, World!");
//        model.addAttribute("title", "Hello");
        model.addAttribute("appNameSet", RegisterAgentFactory.getAppNameSet());

        return "home";
    }

    @ResponseBody
    @RequestMapping("getAppNameSet")
    public Set<String> getAppNameSet(){
        return RegisterAgentFactory.getAppNameSet();
    }

    @ResponseBody
    @RequestMapping("add")
    public Result add(String appName, String mappingPath, String serverPath){
        return Result.ok(jdbcTemplate.update(
                "INSERT INTO t_appname_path(app_name, mapping_path, server_path, create_time) VALUES (?, ?, ?, now())",
                appName, mappingPath, serverPath));
    }
    @ResponseBody
    @RequestMapping("delete")
    public Result delete(int id){
        return Result.ok(jdbcTemplate.update("DELETE FROM t_appname_path where id = ?", id));
    }
    @ResponseBody
    @RequestMapping("update")
    public Result update(int id, String appName, String mappingPath){
        return Result.ok(jdbcTemplate.update("UPDATE t_appname_path set app_name = ?, mappingPath = ? where id = ?",
                appName, mappingPath, id));
    }
    @ResponseBody
    @RequestMapping("select")
    public Result select(String appName, String mappingPath,
                         @RequestParam(required = false, defaultValue = "1") int page,
                         @RequestParam(required = false, defaultValue = "10") int limit){
        String sql = "SELECT * FROM t_appname_path WHERE 1=1";
        if(StringUtils.isNotBlank(appName)){
            sql += " and app_name = '" + appName + "'";
        }
        if(StringUtils.isNotBlank(mappingPath)){
            sql += " and mapping_path = '" + mappingPath + "'";
        }
        Integer count  = jdbcTemplate.queryForObject("select count(*) from (" + sql + ")", Integer.class);

        List<Map<String, Object>> res = new ArrayList<>();
        if(count != 0){
            res = jdbcTemplate.queryForList(sql);
        }
        return Result.okPage(count, res);
    }






}
