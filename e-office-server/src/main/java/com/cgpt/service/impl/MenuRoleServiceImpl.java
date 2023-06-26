package com.cgpt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cgpt.mapper.MenuRoleMapper;
import com.cgpt.pojo.MenuRole;
import com.cgpt.pojo.RespBean;
import com.cgpt.service.IMenuRoleService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MenuRoleServiceImpl extends ServiceImpl<MenuRoleMapper, MenuRole> implements IMenuRoleService {

    @Autowired
    private MenuRoleMapper menuRoleMapper;

    /**
     * 更新角色菜单
     * @param rid
     * @param mids
     * @return
     */
    @Override
    public RespBean updateMenuRole(Integer rid, Integer[] mids) {
        menuRoleMapper.delete(new QueryWrapper<MenuRole>().eq("rid",rid));
        if (mids == null || mids.length==0){
            return RespBean.success("更新成功");
        }
        Integer result = menuRoleMapper.insertRecord(rid, mids);
        if (mids.length == result){
            return RespBean.success("更新成功");
        }
        return RespBean.error("更新失败");
    }
}
