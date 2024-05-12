package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liny.generatorweb.mapper.GeneratorMapper;
import com.liny.generatorweb.model.entity.Generator;
import generator.service.GeneratorService;
import org.springframework.stereotype.Service;

/**
 * @author 邓麟懿
 * @description 针对表【generator(代码生成器)】的数据库操作Service实现
 * @createDate 2024-05-11 12:37:37
 */
@Service
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator>
        implements GeneratorService {

}




