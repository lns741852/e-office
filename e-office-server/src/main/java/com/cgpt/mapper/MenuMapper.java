package com.cgpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgpt.pojo.Menu;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhanglishen
 * @since 2020-11-14
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据用户ID获取菜单
     * @param id
     * @return
     */
    List<Menu> getMenusByAdminId(Integer id);

    /**
     * 根据角色获取菜单列表
     * @return
     */
    List<Menu> getMenusWithRole();

    /**
     * 获取所有目录
     * @return
     */
    List<Menu> getAllMenus();

}
