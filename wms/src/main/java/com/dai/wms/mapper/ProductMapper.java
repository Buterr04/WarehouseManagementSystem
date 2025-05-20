package com.dai.wms.mapper;

import com.dai.wms.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dai
 * @since 2025-04-14
 */

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT p.product_id, p.product_name, p.specifications, p.unit_price, p.stock_quantity, p.supplier_id, s.supplier_name " +
            "FROM product p " +
            "LEFT JOIN supplier s ON p.supplier_id = s.supplier_id")
    List<Product> findAllWithSupplier();
}
