/**
 * 
 */
package adam.test.bean.map;

import java.util.HashMap;
import java.util.Map;

import javax.imageio.plugins.bmp.BMPImageWriteParam;

import org.springframework.adam.common.bean.ResultVo;
import org.springframework.adam.common.bean.map.AdamBeanMapper;

import com.alibaba.fastjson.JSON;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.TypeBuilder;

/**
 * @author USER
 *
 */
public class BeanMapTest {
	
	private static AdamBeanMapper bm = new AdamBeanMapper();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ResultVo<Map<String, String>> resultVo = new ResultVo<Map<String, String>>();
		resultVo.setResultMsg("aaaaaaaaaaa");
		Map<String, String> data = new HashMap<String, String>();
		data.put("a1", "b1");
		data.put("a2", "b2");
		data.put("a3", "b3");
		data.put("a4", "b4");
		resultVo.setData(data);
		ResultVoTmp<Map<String, String>> resultVoTmp = bm.getMapper().map(resultVo, ResultVoTmp.class);
		System.out.println(JSON.toJSON(resultVoTmp));
		Map<String, String> dataTmp = bm.getMapper().mapAsMap(data, new TypeBuilder<Map<String, String>>(){}.build(), new TypeBuilder<Map<String, String>>(){}.build());
		System.out.println(JSON.toJSON(dataTmp));
		ResultVoTmp<Map<String, String>> resultVoTmp1 = bm.getMapper().map(resultVo, new TypeBuilder<ResultVo<Map<String, String>>>(){}.build(), new TypeBuilder<ResultVoTmp<Map<String, String>>>(){}.build());
		System.out.println(JSON.toJSON(resultVoTmp1));
	}

}
