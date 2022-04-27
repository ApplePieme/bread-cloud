package com.breadme.breadcloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.breadme.breadcloud.entity.FileRecord;
import com.breadme.breadcloud.mapper.FileRecordMapper;
import com.breadme.breadcloud.service.FileRecordService;
import org.springframework.stereotype.Service;

/**
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:10
 */
@Service
public class FileRecordServiceImpl extends ServiceImpl<FileRecordMapper, FileRecord> implements FileRecordService {
}
