package com.pepper.metrics.sample.mybatis.runner;

import com.pepper.metrics.sample.mybatis.dao.CityDao;
import com.pepper.metrics.sample.mybatis.mapper.HotelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-12
 */
@Component
public class SampleRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SampleRunner.class);
    @Autowired
    private CityDao cityDao;
    @Autowired
    private HotelMapper hotelMapper;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    @SuppressWarnings("squid:S106")
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                TimeUnit.SECONDS.sleep(10);
                for (;;) {
                    log.debug(String.valueOf(cityDao.selectCityById(1)));
                    hotelMapper.selectByCityId(1);
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            });
        }
    }
}
