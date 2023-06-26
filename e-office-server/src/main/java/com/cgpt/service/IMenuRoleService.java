package com.cgpt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cgpt.pojo.MenuRole;
import com.cgpt.pojo.RespBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhanglishen
 * @since 2020-11-14
 */
public interface IMenuRoleService extends IService<MenuRole> {

    /**
     * 更新角色菜单
     * @param rid
     * @param mids
     * @return
     */
    RespBean updateMenuRole(Integer rid,Integer[] mids);

}
