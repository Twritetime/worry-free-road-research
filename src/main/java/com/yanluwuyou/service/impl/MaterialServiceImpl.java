package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.mapper.MaterialMapper;
import com.yanluwuyou.service.MaterialService;
import org.springframework.stereotype.Service;

/**
 * 资料商城服务实现类
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {
}
