/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api;

import com.alibaba.fastjson2.JSONObject;
import feign.Param;
import feign.RequestLine;
import never.say.never.demo.ent_credit.entity.ChinaRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-04
 */
public interface ChinaRegionApiClient extends HttpApiClient {
    String BASE_HOST = "xzqh.mca.gov.cn";
    String BASE_URL = "http://" + BASE_HOST;

    /**
     * 所有省级行政区划代码
     *
     * @return
     */
    default List<ChinaRegion> getShengJiCodes() {
        JSONObject apiResult = getSubRegionCodes(ChinaRegion.ROOT_CODE);
        List<ChinaRegion> chinaRegionList = new ArrayList<>();
        ChinaRegion chinaRegion;
        for (String code : apiResult.keySet()) {
            chinaRegion = new ChinaRegion();
            chinaRegion.setCode(Integer.parseInt(code) / 1000 + "000");
            chinaRegion.setP_code(ChinaRegion.ROOT_CODE);
            chinaRegion.setType(ChinaRegion.AreaType.SHENG.getValue());
            if ("710000".equals(chinaRegion.getCode())) {
                chinaRegion.setName("台湾省");
            }
            if ("810000".equals(chinaRegion.getCode())) {
                chinaRegion.setName("香港特别行政区");
            }
            if ("820000".equals(chinaRegion.getCode())) {
                chinaRegion.setName("澳门特别行政区");
            }
            chinaRegionList.add(chinaRegion);
        }
        return chinaRegionList;
    }

    /**
     * 所有市级行政区划代码
     *
     * @return
     */
    default List<ChinaRegion> getShiJiCodes(ChinaRegion shengJiRegion) {
        JSONObject apiResult = getSubRegionCodes(shengJiRegion.getCode());
        if (apiResult == null || apiResult.isEmpty()) {
            return null;
        }
        List<ChinaRegion> shiRegionList = new ArrayList<>();
        ChinaRegion shiRegion;
        for (String code : apiResult.keySet()) {
            shiRegion = new ChinaRegion();
            shiRegion.setCode(code);
            shiRegion.setP_code(shengJiRegion.getCode());
            shiRegion.setParent(shengJiRegion);
            shiRegion.setType(ChinaRegion.AreaType.SHI.getValue());
            shiRegionList.add(shiRegion);
        }
        shengJiRegion.setSubList(shiRegionList);
        return shiRegionList;
    }

    /**
     * 所有区/县级行政区划代码
     *
     * @return
     */
    default List<ChinaRegion> getQuXianJiCodes(ChinaRegion shiRegion) {
        JSONObject apiResult = getSubRegionCodes(shiRegion.getCode());
        if (apiResult == null || apiResult.isEmpty()) {
            return null;
        }
        List<ChinaRegion> quXianRegionList = new ArrayList<>();
        ChinaRegion quXianRegion;
        for (String code : apiResult.keySet()) {
            quXianRegion = new ChinaRegion();
            quXianRegion.setCode(code);
            quXianRegion.setP_code(shiRegion.getCode());
            quXianRegion.setType(ChinaRegion.AreaType.QU_XIAN.getValue());
            quXianRegion.setParent(shiRegion);
            quXianRegionList.add(quXianRegion);
        }
        shiRegion.setSubList(quXianRegionList);
        return quXianRegionList;
    }

    /**
     * 下级行政区划代码
     *
     * @param regionCode
     * @return
     */
    @RequestLine("GET /getInfo?code={regionCode}&type=2")
    @feign.Headers("Content-Type: application/json")
    JSONObject getSubRegionCodes(@Param("regionCode") String regionCode);

    /**
     * 省区/县信息
     *
     * @param regionCode
     * @return
     */
    @RequestLine("GET /getInfo?code={regionCode}&type=1")
    @feign.Headers("Content-Type: application/json")
    JSONObject getShengQuXianInfo(@Param("regionCode") String regionCode);
}
