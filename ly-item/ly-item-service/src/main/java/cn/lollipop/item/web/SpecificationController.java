package cn.lollipop.item.web;

import cn.lollipop.item.pojo.SpecGroup;
import cn.lollipop.item.pojo.SpecParam;
import cn.lollipop.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    private final SpecificationService specificationService;

    @Autowired
    public SpecificationController(SpecificationService specificationService) {
        this.specificationService = specificationService;
    }

    /**
     * 根据分类编号查询其全部商品规格分组信息
     *
     * @param cid 分类编号
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 增加商品规格信息
     *
     * @param specGroup 商品规格分组
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<SpecGroup> saveGroup(@RequestBody SpecGroup specGroup) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specificationService.saveGroup(specGroup));
    }

    /**
     * 修改商品规格信息
     *
     * @param specGroup 商品规格分组
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> editGroup(@RequestBody SpecGroup specGroup) {
        specificationService.editGroup(specGroup);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 删除商品规格信息
     *
     * @param gid 商品规格分组编号
     * @return
     */
    @DeleteMapping("group/{gid}")
    public ResponseEntity<SpecGroup> deleteGroup(@PathVariable Long gid) {
        specificationService.deleteSpecGroup(gid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据商品规格分组编号或商品分类编号查询其全部商品规格参数信息
     *
     * @param gid 商品规格分组编号
     * @param cid 商品分类编号
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamList(Long gid, Long cid, Boolean searching) {
        return ResponseEntity.ok(specificationService.queryParamList(gid, cid, searching));
    }

    /**
     * 增加商品规格参数信息
     *
     * @param specParam 商品规格参数
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<SpecParam> saveParam(@RequestBody SpecParam specParam) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specificationService.saveParam(specParam));
    }

    /**
     * 修改商品规格参数信息
     *
     * @param specParam 商品规格参数
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> editParam(@RequestBody SpecParam specParam) {
        specificationService.editParam(specParam);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 删除商品规格参数信息
     *
     * @param pid 商品规格参数编号
     * @return
     */
    @DeleteMapping("param/{pid}")
    public ResponseEntity<Void> deleteParam(@PathVariable Long pid) {
        specificationService.deleteParam(pid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据分类查询规格组及组内参数
     *
     * @param cid
     * @return
     */
    @GetMapping("group")
    public ResponseEntity<List<SpecGroup>> queryListByCid(@RequestParam("cid") Long cid) {
        return ResponseEntity.ok(specificationService.queryListByCid(cid));
    }
}