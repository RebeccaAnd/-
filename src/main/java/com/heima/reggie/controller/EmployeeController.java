package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.reggie.common.R;
import com.heima.reggie.config.entity.Employee;
import com.heima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j //输出台输出日志
@RestController
@RequestMapping("/employee") //登录时发送请求-路径中写了employee
public class EmployeeController {

    @Autowired //把接口注入进来
    private EmployeeService employeeService;

    /*
     * 员工登录
     */
    @PostMapping("/login") //因为前端发送的请求是post请求
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1request是登陆成功员工对象的id 存到session一份,表示登录成功 2接收前端json形式的两个参数

        // 1.将页面提交的密码进行md5加密
        String password = employee.getPassword(); //因为springboot会自动把获取的数据进行封装,所以直接Get
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 2.根据用户名 查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();//实体类包装一个查询对象
        queryWrapper.eq(Employee::getUsername, employee.getUsername()); //查询条件 等值查询, 封装好
        Employee emp = employeeService.getOne(queryWrapper); //用户名有唯一约束,所以getOne()查-封装成一个Employee对象

        // 3.如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }

        // 4.密码比对,如果不一致则返回登陆失败
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        // 5. 查看员工状态,如果为已禁用状态,则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        // 6.登陆成功,将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId()); //放到Session
        return R.success(emp);
    }

    /*
     * 员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清除Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    /*
     * 新增员工
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) { //只用到了code;传过来的参数封装成一个JSON对象 +需要一个request对象
//        log.info("新增员工,员工信息:{}", employee.toString()); //->输出, 数据是可以正常封装的
        //发现封装之后密码是空的,设置初始密码123456,需要进行md5加密处理;status给了默认值不用手动设置
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now()); //获取当前系统时间

        //employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee); //对象传进去
        //save()不是自己写的,是继承了IService接口,提供了save()

        return R.success("新增员工成功");
    }

    /*
     * 员工信息的分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) { //分页方法;泛型不是随便来的,要和前端页面配合,说白了就是Page需要什么就传入什么
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize); //传过来的参数默认1,10

        //构造条件构造器 动态构造
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name); //就是name不等于空的时候,才会添加这个条件
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /*
     * 根据id修改员工信息
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee) {  //因为页面只要string
        log.info(employee.toString());

        Long empId = (Long)request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /*
     * 根据ID查询d.html页面为公共页面,新增员工和编辑员工都是在此页面操作
     * 所以该员工信息
     * 注意,ad代码部分和之前添加员工的代码对应,不需要重写
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据员工ID查询信息");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }


}
