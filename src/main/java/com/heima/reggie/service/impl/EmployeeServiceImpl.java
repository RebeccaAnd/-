package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.config.entity.Employee;
import com.heima.reggie.mapper.EmployeeMapper;
import com.heima.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service //由Spring来管理他
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
