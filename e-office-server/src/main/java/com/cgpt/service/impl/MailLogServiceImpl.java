package com.cgpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cgpt.mapper.MailLogMapper;
import com.cgpt.pojo.MailLog;
import com.cgpt.service.IMailLogService;
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
public class MailLogServiceImpl extends ServiceImpl<MailLogMapper, MailLog> implements IMailLogService {

}
