package com.cgpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cgpt.mapper.RoleMapper;
import com.cgpt.pojo.Role;
import com.cgpt.service.IRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhanglishen
 * @since 2020-11-14
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
