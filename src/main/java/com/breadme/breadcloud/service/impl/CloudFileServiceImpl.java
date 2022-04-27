package com.breadme.breadcloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.breadme.breadcloud.entity.CloudFile;
import com.breadme.breadcloud.mapper.CloudFileMapper;
import com.breadme.breadcloud.service.CloudFileService;
import org.springframework.stereotype.Service;

/**
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:08
 */
@Service
public class CloudFileServiceImpl extends ServiceImpl<CloudFileMapper, CloudFile> implements CloudFileService {
}
