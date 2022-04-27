package com.breadme.breadcloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.breadme.breadcloud.entity.Directory;
import com.breadme.breadcloud.mapper.DirectoryMapper;
import com.breadme.breadcloud.service.DirectoryService;
import org.springframework.stereotype.Service;

/**
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:09
 */
@Service
public class DirectoryServiceImpl extends ServiceImpl<DirectoryMapper, Directory> implements DirectoryService {
}
