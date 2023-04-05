package com.zzp.seckilldemo.mapper;

import com.zzp.seckilldemo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author roc
 * @since 2023-03-19
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {


}
