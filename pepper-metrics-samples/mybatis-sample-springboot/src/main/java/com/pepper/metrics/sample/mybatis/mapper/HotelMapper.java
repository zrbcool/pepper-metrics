package com.pepper.metrics.sample.mybatis.mapper;

import com.pepper.metrics.sample.mybatis.domain.Hotel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HotelMapper {

  Hotel selectByCityId(int cityId);

}
