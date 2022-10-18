package me.qiujun.arbitrage.mapper;

import me.qiujun.arbitrage.bean.base.Config;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ConfigMapper {

    Config selectById(Long id);

    List<Config> selectAll();

}