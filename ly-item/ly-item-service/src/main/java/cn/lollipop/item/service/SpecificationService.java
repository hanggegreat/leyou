package cn.lollipop.item.service;

import cn.lollipop.common.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.item.mapper.SpecGroupMapper;
import cn.lollipop.item.mapper.SpecParamMapper;
import cn.lollipop.item.pojo.Brand;
import cn.lollipop.item.pojo.SpecGroup;
import cn.lollipop.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpecificationService {

    private final SpecGroupMapper specGroupMapper;
    private final SpecParamMapper specParamMapper;

    @Autowired
    public SpecificationService(SpecGroupMapper specGroupMapper, SpecParamMapper specParamMapper) {
        this.specGroupMapper = specGroupMapper;
        this.specParamMapper = specParamMapper;
    }

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionConstant.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    @Transactional
    public void editGroup(SpecGroup specGroup) {
        if (specGroup == null || specGroup.getId() == null) {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }

        Example example = new Example(Brand.class);
        example.createCriteria().andEqualTo("id", specGroup.getId());
        if (specGroupMapper.updateByExample(specGroup, example) != 1) {
            throw new LyException(ExceptionConstant.SPEC_GROUP_UPDATE_ERROR);
        }
    }

    public SpecGroup saveGroup(SpecGroup specGroup) {
        if (specGroup == null) {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }

        if (specGroupMapper.insert(specGroup) != 1) {
            throw new LyException(ExceptionConstant.SPEC_GROUP_SAVE_ERROR);
        }

        return specGroup;
    }

    public void deleteSpecGroup(Long gid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setId(gid);
        specGroupMapper.delete(specGroup);
    }

    public SpecParam saveParam(SpecParam specParam) {
        if (specParam == null) {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }

        if (specParamMapper.insert(specParam) != 1) {
            throw new LyException(ExceptionConstant.SPEC_PARAM_SAVE_ERROR);
        }

        return specParam;
    }

    @Transactional
    public void editParam(SpecParam specParam) {
        if (specParam == null || specParam.getId() == null) {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }

        if (specParamMapper.update(specParam) != 1) {
            throw new LyException(ExceptionConstant.SPEC_PARAM_UPDATE_ERROR);
        }
    }

    public void deleteParam(Long pid) {
        if (pid == null) {
            throw new LyException(ExceptionConstant.INVALID_PARAM);
        }

        SpecParam specParam = new SpecParam();
        specParam.setId(pid);
        specParamMapper.delete(specParam);
    }

    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> list = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionConstant.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }
}