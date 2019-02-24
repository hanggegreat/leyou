package cn.lollipop.item.mapper;

import cn.lollipop.common.mapper.BaseMapper;
import cn.lollipop.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Update;

public interface SpecParamMapper extends BaseMapper<SpecParam, Long> {

    @Update("UPDATE tb_spec_param " +
            "SET cid=#{cid}, group_id=#{groupId}, name=#{name}, `numeric`= #{numeric}, unit=#{unit}, generic=#{generic}, searching=#{searching}, segments=#{segments}" +
            "WHERE id=#{id}")
    int update(SpecParam specParam);
}
