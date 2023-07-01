package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        // 将密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根据页面提交的name查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if (emp == null) {
            return R.error("登录失败");
        }

        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        // 查看员工状态是否被禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已被禁用");
        }

        // 登陆成功，把用户ID放到session中
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    // add  类上已经有employee接口了
    @PostMapping
   public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 采用公共字段赋值
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        Long emp = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(emp);
//        employee.setUpdateUser(emp);

        employeeService.save(employee);
        return R.success("新增员工成功");
   }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
   @GetMapping("/page")
   public R<Page> page(int page, int pageSize, String name) {
        log.info("page={}, pageSize={}, name={}", page, pageSize, name);

        // 构造分页构造器
       Page pageInfo = new Page<>(page, pageSize);

       // 构造条件构造器
       LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
       // 增加过滤条件
       queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

       // 增加排序条件
       queryWrapper.orderByDesc(Employee::getUpdateTime);
       // 执行查询
       employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
   }

    /**
     * 更新用户信息
     * @param request
     * @param employee
     * @return
     */
   @PutMapping
   public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
//       Long empId = (Long) request.getSession().getAttribute("employee");
//       employee.setUpdateUser(empId);
//       employee.setUpdateTime(LocalDateTime.now());

       employeeService.updateById(employee);
       return R.success("修改成功");
   }

   @GetMapping("/{id}")
   public R<Employee> getById(@PathVariable String id) {
       Employee employee = employeeService.getById(id);
       if (employee != null) {
           return R.success(employee);
       }

       return R.error("没有查询到信息");

   }
}
