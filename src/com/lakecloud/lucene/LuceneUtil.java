package com.lakecloud.lucene;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKSimilarity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Evaluate;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.User;

/**
 * @info lucene搜索工具类,用来写入索引，搜索数据
 * @version 1.0
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
public class LuceneUtil {

	private static File index_file = null;
	private static Analyzer analyzer = null;
	private static LuceneUtil lucence = new LuceneUtil();
	private static QueryParser parser;
	private static String index_path;
	private int textmaxlength = 2000;
	private static String prefixHTML = "<font color='red'>";
	private static String suffixHTML = "</font>";
	private int pageSize = 20;

	/** 初始化工具 * */
	public LuceneUtil() {
		// System.out.println("lucene初始化开始....");
		analyzer = new IKAnalyzer();
		parser = new ShopQueryParser(Version.LUCENE_35, LuceneVo.TITLE,
				analyzer);
	}

	/** 返回一个单例 * */
	public static LuceneUtil instance() {
		return lucence;
	}

	public static void setIndex_path(String index_path) {
		LuceneUtil.index_path = index_path;
	}

	/**
	 * 搜索框根据关键字
	 * 索引库中查询
	 * @param keyword
	 * @return
	 * @throws IOException
	 * @erikchang
	 */
	public LuceneResult searchIndex(BooleanQuery query, int start, int size,
			double begin_price, double end_price, Sort sort,LuceneResult pList) throws IOException {
		IndexSearcher searcher = null;
		IndexReader reader = null;
		try {
			index_file = new File(index_path);
			reader = IndexReader.open(FSDirectory.open(index_file));
			searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new IKSimilarity());
			/*if (keyword.indexOf("title:") < 0) {
				keyword = "(title:" + keyword + " OR content:" + keyword + " OR seo_keywords:"+keyword+" OR goods_serial:"+keyword
				+" OR goods_brand:"+keyword+" OR goods_sepc:"+keyword+" OR goods_store_name:"+keyword+")";
			}
			if (begin_price >= 0 && end_price > 0) {
				keyword = keyword + " AND store_price:[" + begin_price + " TO "
						+ end_price + "]";
			}
			// System.out.println("查询条件：" + keyword);
			parser.setAllowLeadingWildcard(true);
			Query query = parser.parse(keyword);*/
			TopDocs topDocs = null;
			if (sort != null) {
				topDocs = searcher.search(query, (size + start), sort);
			} else {
				topDocs = searcher.search(query, (size + start));
			}
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			int end = (size + start) < topDocs.totalHits ? (size + start)
					: topDocs.totalHits;
			//处理 lucence query 
			pList=processLucenceQuery(reader,query,searcher,scoreDocs,start,end,pList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			searcher.close();
			reader.close();
		}
		return pList;
	}
	
	/**
	 * 根据条件查询商品
	 * 索引库中查询
	 * @param keyword
	 * @return
	 * @throws IOException
	 * @erikchang
	 */
	public LuceneResult searchIndexByConditions(Query query, int start, int size,
			Sort sort,LuceneResult pList) throws IOException {
		IndexSearcher searcher = null;
		IndexReader reader = null;
		try {
			index_file = new File(index_path);
			reader = IndexReader.open(FSDirectory.open(index_file));
			searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new IKSimilarity());
			if(query!=null ){
				/*parser.setAllowLeadingWildcard(true);
				Query query = parser.parse(lucenceSQL);*/
				TopDocs topDocs = null;
				if (sort != null) {
					topDocs = searcher.search(query, (size + start), sort);
				} else {
					topDocs = searcher.search(query, (size + start));
				}
				ScoreDoc[] scoreDocs = topDocs.scoreDocs;
				int end = (size + start) < topDocs.totalHits ? (size + start)
						: topDocs.totalHits;
				//处理 lucence query
				pList=processLucenceQuery(reader,query,searcher,scoreDocs,start,end,pList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			searcher.close();
			reader.close();
		}
		return pList;
	}
	
	/**
	 * 处理lucence Query
	 * @return
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * @throws IOException 
	 * @throws InvalidTokenOffsetsException 
	 */
	public LuceneResult processLucenceQuery(IndexReader reader,Query query,IndexSearcher searcher,ScoreDoc[] scoreDocs,int start,int end
			,LuceneResult pList) throws CorruptIndexException, IOException, InvalidTokenOffsetsException{
		List<LuceneVo> volist = new ArrayList<LuceneVo>();
		List<Goods> goodsList = new ArrayList<Goods>();
		for (int i = start; i < end; i++) {
			Document doc = searcher.doc(scoreDocs[i].doc);
			LuceneVo vo = new LuceneVo();
			Goods goods = new Goods();
			// 关键词加亮
			SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
					prefixHTML, suffixHTML);
			Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
					new QueryScorer(query));
			highlighter.setTextFragmenter(new SimpleFragmenter(
					textmaxlength));
			String content = highlighter.getBestFragment(analyzer,
					LuceneVo.CONTENT, doc.get(LuceneVo.CONTENT));
			String title = highlighter.getBestFragment(analyzer,
					LuceneVo.TITLE, doc.get(LuceneVo.TITLE));
			vo.setVo_id(CommUtil.null2Long(doc.get(LuceneVo.ID)));
			goods.setId(vo.getVo_id());//商品ID
			vo.setVo_add_time(CommUtil.null2Long(doc.get(LuceneVo.ADD_TIME)));
			goods.setAddTime(new Date(vo.getVo_add_time()));//添加时间 
			vo.setVo_store_price(CommUtil.null2Double(doc
					.get(LuceneVo.STORE_PRICE)));
			goods.setStore_price(new BigDecimal(vo.getVo_store_price()));//店铺价格
			if(doc.get(LuceneVo.GOODS_SERIAL)==null){
				continue;
			}
			String seo_keywords= highlighter.getBestFragment(analyzer,
					LuceneVo.SEO_KEYWORDS, doc.get(LuceneVo.SEO_KEYWORDS));
			String goods_serial= highlighter.getBestFragment(analyzer,
					LuceneVo.GOODS_SERIAL, doc.get(LuceneVo.GOODS_SERIAL));
			String goods_brand= highlighter.getBestFragment(analyzer,
					LuceneVo.GOODS_BRAND, doc.get(LuceneVo.GOODS_BRAND));
			String goods_store= highlighter.getBestFragment(analyzer,
					LuceneVo.GOODS_STORE, doc.get(LuceneVo.GOODS_STORE));
			String store_ower= highlighter.getBestFragment(analyzer,
					LuceneVo.STORE_OWER, doc.get(LuceneVo.STORE_OWER));
			vo.setVo_content(null2docValue(content,doc,LuceneVo.CONTENT));
			goods.setGoods_details(vo.getVo_content());//商品描述
			vo.setVo_title(null2docValue(title,doc,LuceneVo.TITLE));
			goods.setGoods_name(vo.getVo_title());//商品名称
			vo.setVo_seo_keywords(null2docValue(seo_keywords,doc,LuceneVo.SEO_KEYWORDS));
			goods.setSeo_keywords(vo.getVo_seo_keywords());//搜索关键词
			vo.setVo_goods_serial(null2docValue(goods_serial,doc,LuceneVo.GOODS_SERIAL));
			goods.setGoods_serial(vo.getVo_goods_serial());//货号
			vo.setVo_goods_brand(null2docValue(goods_brand,doc,LuceneVo.GOODS_BRAND));
			vo.setVo_brand_id(CommUtil.null2String(doc.get(LuceneVo.BRAND_ID)));
			GoodsBrand brand=new GoodsBrand();
			brand.setName(vo.getVo_goods_brand());
			brand.setId(vo.getVo_brand_id()==null?null
					:"".equals(vo.getVo_brand_id())?null:Long.parseLong(vo.getVo_brand_id()));
			goods.setGoods_brand(brand);//品牌
			vo.setVo_store_id(CommUtil.null2String(doc.get(LuceneVo.STORE_ID)));
			vo.setVo_goods_store(null2docValue(goods_store,doc,LuceneVo.GOODS_STORE));
			vo.setVo_store_ower(store_ower);
			vo.setVo_store_grade(CommUtil.null2String(doc.get(LuceneVo.STORE_GRADE)));
			vo.setVo_user_id(CommUtil.null2Long(doc.get(LuceneVo.USER_ID)));
			vo.setVo_city_id(CommUtil.null2Long(doc.get(LuceneVo.CIT_ID)));
			vo.setVo_city_name(CommUtil.null2String(doc.get(LuceneVo.CITY_NAME)));
			vo.setVo_weixin_status(CommUtil.null2String(doc.get(LuceneVo.WEIXIN_STATUS)));
			Store store=new Store();
			store.setStore_name(vo.getVo_goods_store());//商店名称
			store.setId(vo.getVo_store_id()==null?null:Long.parseLong(vo.getVo_store_id()));//商店ID
			store.setWeixin_status(vo.getVo_weixin_status()==null?0
					:"".equals(vo.getVo_weixin_status())?0:Integer.parseInt(vo.getVo_weixin_status()));//app商品status
			store.setStore_ower(vo.getVo_store_ower());
			StoreGrade grade=new StoreGrade();
			grade.setGradeName(vo.getVo_store_grade());//商店等级名称
			store.setGrade(grade);
			User user = new User();
			user.setId(vo.getVo_user_id());
			store.setUser(user);
			Area area = new Area();
			Area parent =new Area();
			parent.setId(vo.getVo_city_id());
			parent.setAreaName(vo.getVo_city_name());//城市名称
			area.setParent(parent);
			store.setArea(area);//设置店铺所属城市ID
			goods.setGoods_store(store);//商店
			vo.setVo_goods_click(CommUtil.null2Int(doc.get(LuceneVo.GOODS_CLICK)));
			goods.setGoods_click(vo.getVo_goods_click());//商品浏览次数
			vo.setVo_goods_collect(CommUtil.null2Int(doc.get(LuceneVo.GOODS_COLLECT)));
			goods.setGoods_collect(vo.getVo_goods_collect());//商品收藏次数
			vo.setVo_img_path(CommUtil.null2String(doc.get(LuceneVo.IMG_PATH)));
			vo.setVo_img_name(CommUtil.null2String(doc.get(LuceneVo.IMG_NAME)));
			vo.setVo_img_ext(CommUtil.null2String(doc.get(LuceneVo.IMG_EXT)));
			Accessory mainPhoto= new Accessory();
			mainPhoto.setPath(vo.getVo_img_path());
			mainPhoto.setName(vo.getVo_img_name());
			mainPhoto.setExt(vo.getVo_img_ext());
			goods.setGoods_main_photo(mainPhoto);//商品主图片
			vo.setVo_goods_inventory_detail(CommUtil.null2String(doc.get(LuceneVo.GOODS_INVENTORY_DETAIL)));
			goods.setGoods_inventory_detail(vo.getVo_goods_inventory_detail());//规格库存情况
			vo.setVo_goods_sepc(CommUtil.null2String(doc.get(LuceneVo.GOODS_SPEC)));
			List<GoodsSpecProperty> goods_specs = new ArrayList<GoodsSpecProperty>();
			goods.setGoods_specs(goods_specs);//规格属性处理
			vo.setVo_evalutes_num(CommUtil.null2Int(doc.get(LuceneVo.EVALUATES_NUM)));
			List<Evaluate> evaluates=new ArrayList<Evaluate>(vo.getVo_evalutes_num());
			goods.setEvaluates(evaluates);//累积评论数
			vo.setVo_inventory_type(CommUtil.null2String(doc.get(LuceneVo.INVENTORY_TYPE)));
			goods.setInventory_type(vo.getVo_inventory_type());//库存类型
			vo.setVo_goods_inventory(CommUtil.null2Int(doc.get(LuceneVo.GOODS_INVENTORY)));
			goods.setGoods_inventory(vo.getVo_goods_inventory());//库存数量
			vo.setVo_goods_price(CommUtil.null2Double(doc
					.get(LuceneVo.GOODS_PRICE)));
			goods.setGoods_price(new BigDecimal(vo.getVo_goods_price()));//商品原价格
			vo.setVo_goods_current_price(CommUtil.null2Double(doc
					.get(LuceneVo.GOODS_CURRENT_PRICE)));
			goods.setGoods_current_price(new BigDecimal(vo.getVo_goods_current_price()));//商品现金
			vo.setVo_goods_status(CommUtil.null2String(doc.get(LuceneVo.GOODS_STATUS)));
			goods.setGoods_status(vo.getVo_goods_status()==null?0:"".equals(vo.getVo_goods_status())?0:Integer.parseInt(vo.getVo_goods_status()));//商品上架状态
			vo.setVo_goods_salenum(CommUtil.null2Int(doc.get(LuceneVo.GOODS_SALENUM)));//销量
			goods.setGoods_salenum(vo.getVo_goods_salenum());//商品销量
			volist.add(vo);
			goodsList.add(goods);
			
		}
		pList.setVo_list(volist);
		pList.setGoods_list(goodsList);
		return pList;
	}

	public  String null2docValue(String value,Document doc,String key){
		if (value == null) {
			value=doc.get(key);
		}
		return value;
	}
	/**
	 * 
	 * @param params
	 * @param after
	 * @return
	 */
	public LuceneResult search(String params, int pageNo,int pageSize, double begin_price,
			double end_price, ScoreDoc after, Sort sort,String[] keys,String[] values
			,BooleanClause.Occur[] clauses,BooleanQuery queryWhere,List<Query>  rangeQuerys) {
		this.pageSize=pageSize;
		LuceneResult pList = new LuceneResult();
		IndexSearcher isearcher = null;
		//List<LuceneVo> list = new ArrayList<LuceneVo>();
		IndexReader reader = null;
		boolean flag=false;
		try {
			// 创建索引搜索器 且只读
			index_file = new File(index_path);
			if (!index_file.exists()) {
				return pList;
			}
			reader = IndexReader.open(FSDirectory.open(index_file), true);
			isearcher = new IndexSearcher(reader);
			// 在索引器中使用IKSimilarity相似度评估器
			isearcher.setSimilarity(new IKSimilarity());
			BooleanQuery multyQuery = new BooleanQuery();//多条件查询
			params=params==null?"":params;
			if(!CommUtil.null2String(params).equals("")){
				if (params!=null && !"".equals(params)&& params.indexOf("title:") < 0 ) {
					flag=true;
					params = "(title:" + params + " OR content:" + params + " OR seo_keywords:"+params+" OR goods_serial:"+params
					+" OR goods_brand:"+params+" OR goods_sepc:"+params+" OR goods_store_name:"+params+")";
				}
				if(keys!=null&&keys.length>0){
					
					for(int i=0;i<keys.length;i++){
						if(i!=0||flag){
							if(BooleanClause.Occur.MUST.equals(clauses[i])){
								params+=" AND ";
							}else if(BooleanClause.Occur.SHOULD.equals(clauses[i])){
								params+=" OR ";
							}else if(BooleanClause.Occur.MUST_NOT.equals(clauses[i])){
								params+=" NOT ";
							}
						}
						flag=true;
						params+=" "+keys[i]+":"+values[i];
					}
				}
				Query queryKey=parser.parse(params);
				multyQuery.add(queryKey, Occur.MUST);
			}
			if(queryWhere!=null){//特殊处理
				flag=true;
				multyQuery.add(queryWhere, Occur.MUST);
			}
			//处理多条件查询
			if(rangeQuerys!=null && rangeQuerys.size()>0){
				for (Query rangeQuery :rangeQuerys) {
					multyQuery.add(rangeQuery,Occur.MUST);
				}
			}
			if (begin_price >= 0 && end_price > 0) {
				flag=true;
				/*params = params + " AND goods_current_price:[" + begin_price + " TO "
						+ end_price + "]";*/
				Query queryRange = NumericRangeQuery.newDoubleRange("goods_current_price", begin_price, end_price, true, true);//parser.parse(params);
				multyQuery.add(queryRange, Occur.MUST);  
			}
			if(flag){//如果有筛选条件
				parser.setAllowLeadingWildcard(true);
				TopDocs topDocs = isearcher
						.search( multyQuery, 10000);
				System.out.println("共有：" + topDocs.totalHits);
				int pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize; // 记算总页数
				int intPageNo = (pageNo > pages ? pages : pageNo);
				if (intPageNo < 1)
					intPageNo = 1;
				pList = this.searchIndex(multyQuery, (intPageNo - 1)
						* this.pageSize, this.pageSize, begin_price, end_price,
						sort,pList);
				pList.setPages(pages);
				pList.setRows(topDocs.totalHits);
				pList.setCurrentPage(intPageNo);
				//pList.setVo_list(vo_list);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (isearcher != null) {
				try {
					isearcher.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return pList;
	}
	
	/**
	 * 按条件搜索lucence
	 * @param params
	 * @param after
	 * @return
	 */
	public LuceneResult searchByConditions(String[]keys,String[]values,BooleanClause.Occur[] clauses,int pageNo, int pageSize,
			double begin_price,double end_price,ScoreDoc after, Sort sort) {
		LuceneResult pList = new LuceneResult();
		IndexSearcher isearcher = null;
		IndexReader reader = null;
		this.pageSize=pageSize;
		//String lucenceSQL="";
		try {
			// 创建索引搜索器 且只读
			index_file = new File(index_path);
			if (!index_file.exists()) {
				return pList;
			}
			reader = IndexReader.open(FSDirectory.open(index_file), true);
			isearcher = new IndexSearcher(reader);
			// 在索引器中使用IKSimilarity相似度评估器
			isearcher.setSimilarity(new IKSimilarity());
			
			/*if(keys!=null&&keys.size()>0){
				//lucenceSQL="(";
				for(int i=0;i<keys.size();i++){
					if(i==0){
						lucenceSQL+= keys.get(i)+":" + values.get(i)+" ";
					}else{
						lucenceSQL+=where.get(i)+" "+keys.get(i)+":" + values.get(i)+" ";
					}
				}
				//lucenceSQL+=")";
			}
			if (begin_price >= 0 && end_price > 0) {
				lucenceSQL = lucenceSQL + " AND store_price:[" + begin_price + " TO "
						+ end_price + "]";
			}*/
			
	        Query query = MultiFieldQueryParser.parse(Version.LUCENE_35,values,keys, clauses,analyzer);
			/*parser.setAllowLeadingWildcard(true);
			Query query = parser.parse(lucenceSQL);*/
			TopDocs topDocs = isearcher.searchAfter(after, query, this.pageSize);
			int pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize; // 记算总页数
			System.out.println("总记录数："+topDocs.totalHits+",总页数："+pages+",大小："+this.pageSize);
			int intPageNo = (pageNo > pages ? pages : pageNo);
			if (intPageNo < 1)
				intPageNo = 1;
			pList = this.searchIndexByConditions(query, (intPageNo - 1)
					* this.pageSize, this.pageSize,
					sort,pList);
			pList.setPages(pages);
			pList.setRows(topDocs.totalHits);
			pList.setCurrentPage(intPageNo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (isearcher != null) {
				try {
					isearcher.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return pList;
	}

	/**
	 * 添加列表到索引库中
	 * 
	 * @erikchang
	 * @param list
	 * @throws IOException
	 */
	public void writeIndex(List<LuceneVo> list) throws IOException {
		IndexWriter writer = openIndexWriter();
		try {
			for (LuceneVo lucenceVo : list) {
				Document document = builderDocument(lucenceVo);
				writer.addDocument(document);
			}
			writer.optimize();
		} finally {
			writer.close();
		}
	}

	/**
	 * 添加单个到索引库中
	 * 
	 * @erikchang
	 * @param LuceneVo
	 * @throws IOException
	 */
	public void writeIndex(LuceneVo vo) {
		IndexWriter writer = null;
		try {
			writer = openIndexWriter();
			Document document = builderDocument(vo);
			writer.addDocument(document);
			writer.optimize();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新索引
	 */
	public void update(String id, LuceneVo vo) {
		IndexWriter writer=null;
		try {
			index_file = new File(index_path);
			Directory directory = FSDirectory.open(index_file);
			IndexWriterConfig writerConfig = new IndexWriterConfig(
					Version.LUCENE_35, analyzer);
			writer = new IndexWriter(directory, writerConfig);; 
			Document doc = builderDocument(vo);
			Term term = new Term("id", String.valueOf(id));
			writer.updateDocument(term, doc);

			writer.close();
		} catch (Exception e) {
			try {
				writer.close();
			} catch (CorruptIndexException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/**
	 * 删除索引文件
	 * 
	 * @param id
	 */
	public void delete_index(String id) {
		try {
			index_file = new File(index_path);
			Directory directory = FSDirectory.open(index_file);
			IndexWriterConfig writerConfig = new IndexWriterConfig(
					Version.LUCENE_35, analyzer);
			IndexWriter writer = new IndexWriter(directory, writerConfig);
			Term term = new Term("id", String.valueOf(id));
			writer.deleteDocuments(term);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除所有索引库
	 * 
	 * @erikchang
	 */
	public void deleteAllIndex(boolean isdeletefile) {
		IndexReader reader = null;
		index_file = new File(index_path);
		if (index_file.exists() && index_file.isDirectory()) {
			try {
				reader = IndexReader.open(FSDirectory.open(index_file), false);
				for (int i = 0; i < reader.maxDoc(); i++) {
					reader.deleteDocument(i);
				}
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			}
			deleteAllFile();
		}
	}

	/**
	 * 删除所有索引文件
	 * 
	 * @erikchang
	 */
	private void deleteAllFile() {
		index_file = new File(index_path);
		File[] files = index_file.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	private IndexWriter openIndexWriter() throws IOException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_35, analyzer);
		// 索引 设置为追加或者覆盖
		index_file = new File(index_path);
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		Directory dir = FSDirectory.open(index_file);
		indexWriterConfig.setRAMBufferSizeMB(256.0);
		IndexWriter writer = new IndexWriter(dir,
				indexWriterConfig);
		writer.setMaxBufferedDocs(100);
		return writer;
	}

	/**
	 * 创建索引文件
	 * @param luceneVo
	 * @return
	 */
	@SuppressWarnings("static-access")
	private Document builderDocument(LuceneVo luceneVo) {
		Document document = new Document();
		Whitelist white = new Whitelist();
		Field type = new Field(luceneVo.TYPE, luceneVo.getVo_type(),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		type.setBoost(12.0f);
		Field id = new Field(luceneVo.ID, String.valueOf(luceneVo.getVo_id()),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		Field title = new Field(luceneVo.TITLE, Jsoup.clean(
				luceneVo.getVo_title(), white.none()), Field.Store.YES,
				Field.Index.ANALYZED);
		title.setBoost(10.0f);
		Field goods_serial = new Field(luceneVo.GOODS_SERIAL, Jsoup.clean(
				luceneVo.getVo_goods_serial(), white.none()), Field.Store.YES,
				Field.Index.ANALYZED);
		goods_serial.setBoost(9.0f);
		Field seo_keywords = new Field(luceneVo.SEO_KEYWORDS, Jsoup.clean(
				luceneVo.getVo_seo_keywords(), white.none()), Field.Store.YES,
				Field.Index.ANALYZED);
		seo_keywords.setBoost(8.0f);
		Field content = new Field(luceneVo.CONTENT, Jsoup.clean(
				luceneVo.getVo_content(), white.none()), Field.Store.YES,
				Field.Index.ANALYZED);
		content.setBoost(7.0f);
		Field goods_brand = new Field(luceneVo.GOODS_BRAND, Jsoup.clean(
				luceneVo.getVo_goods_brand(), white.none()), Field.Store.YES,
				Field.Index.ANALYZED);
		goods_brand.setBoost(6.0f);
		Field goods_sepc = new Field(luceneVo.GOODS_SPEC, Jsoup.clean(
				luceneVo.getVo_goods_sepc(), white.none()), Field.Store.YES,
				Field.Index.ANALYZED);
		goods_sepc.setBoost(4.0f);
		Field store_name = new Field(luceneVo.GOODS_STORE, Jsoup.clean(
				luceneVo.getVo_goods_store(), white.none()), Field.Store.YES,
				Field.Index.ANALYZED);
		store_name.setBoost(2.0f);
		Field store_ower = new Field(luceneVo.STORE_OWER, Jsoup.clean(
				luceneVo.getVo_store_ower(), white.none()), Field.Store.YES,
				Field.Index.ANALYZED);
		store_ower.setBoost(2.0f);
		Field goods_inventory_detail = new Field(luceneVo.GOODS_INVENTORY_DETAIL, Jsoup.clean(
				luceneVo.getVo_goods_inventory_detail(), white.none()), Field.Store.YES,
				Field.Index.ANALYZED);
		
		Field goods_promotion = new Field(luceneVo.GOODS_PROMOTION, Jsoup.clean(
				CommUtil.null2String(luceneVo.getVo_goods_promotion()), 
						white.none()), Field.Store.YES,Field.Index.NOT_ANALYZED);
		/*NumericField goods_price = new NumericField(luceneVo.GOODS_PRICE,
				Field.Store.YES, true);
		goods_price.setDoubleValue(luceneVo.getVo_goods_price());
		NumericField goods_current_price = new NumericField(luceneVo.GOODS_CURRENT_PRICE,
				Field.Store.YES, true);
		goods_current_price.setDoubleValue(luceneVo.getVo_goods_current_price());*/
		NumericField store_price = new NumericField(luceneVo.STORE_PRICE,
				Field.Store.YES, true);
		store_price.setDoubleValue(luceneVo.getVo_store_price());
		NumericField goods_price = new NumericField(luceneVo.GOODS_PRICE,
				Field.Store.YES, true);
		goods_price.setDoubleValue(luceneVo.getVo_goods_price());
		NumericField goods_current_price = new NumericField(luceneVo.GOODS_CURRENT_PRICE,
				Field.Store.YES, true);
		goods_current_price.setDoubleValue(luceneVo.getVo_goods_current_price());
		NumericField description_evaluate = new NumericField(luceneVo.DESCRIPTION_EVALUATE,
				Field.Store.YES, true);
		description_evaluate.setDoubleValue(luceneVo.getVo_description_evaluate()==null?5:luceneVo.getVo_description_evaluate().doubleValue());
		Field storeId = new Field(luceneVo.STORE_ID,
				CommUtil.null2String(luceneVo.getVo_store_id()),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		NumericField userId = new NumericField(luceneVo.USER_ID,
				Field.Store.YES, true);
		userId.setLongValue(luceneVo.getVo_user_id());
		NumericField goods_click = new NumericField(luceneVo.GOODS_CLICK,
				Field.Store.YES, true);
		goods_click.setIntValue(luceneVo.getVo_goods_click());
		NumericField goods_collect = new NumericField(luceneVo.GOODS_COLLECT,
				Field.Store.YES, true);
		goods_collect.setIntValue(luceneVo.getVo_goods_collect());
		Field img_path = new Field(luceneVo.IMG_PATH,
				CommUtil.null2String(luceneVo.getVo_img_path()),
				Field.Store.YES, Field.Index.NO);
		Field img_name = new Field(luceneVo.IMG_NAME,
				CommUtil.null2String(luceneVo.getVo_img_name()),
				Field.Store.YES, Field.Index.NO);
		Field img_ext = new Field(luceneVo.IMG_EXT,
				CommUtil.null2String(luceneVo.getVo_img_ext()),
				Field.Store.YES, Field.Index.NO);
		Field city_id = new Field(luceneVo.CIT_ID,
				CommUtil.null2String(luceneVo.getVo_city_id()),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		NumericField evalutes_num = new NumericField(luceneVo.EVALUATES_NUM,
				Field.Store.YES, true);
		evalutes_num.setIntValue(luceneVo.getVo_evalutes_num());
		Field store_grade = new Field(luceneVo.STORE_GRADE,
				CommUtil.null2String(luceneVo.getVo_store_grade()),
				Field.Store.YES, Field.Index.NO);
		/*Field add_time = new Field(luceneVo.ADD_TIME,
				CommUtil.null2String(luceneVo.getVo_add_time()),
				Field.Store.YES, Field.Index.NOT_ANALYZED);*/
		NumericField add_time = new NumericField(luceneVo.ADD_TIME,
				Field.Store.YES,true);
		add_time.setLongValue(luceneVo.getVo_add_time());
		Field goods_salenum = new Field(luceneVo.GOODS_SALENUM,
				CommUtil.null2String(luceneVo.getVo_goods_salenum()),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		Field inventory_type = new Field(luceneVo.INVENTORY_TYPE,
				CommUtil.null2String(luceneVo.getVo_inventory_type()),
				Field.Store.YES, Field.Index.NO);
		NumericField goods_inventory = new NumericField(luceneVo.GOODS_INVENTORY,
				Field.Store.YES,false);
		goods_inventory.setIntValue(luceneVo.getVo_goods_inventory());
		Field city_name = new Field(luceneVo.CITY_NAME,
				CommUtil.null2String(luceneVo.getVo_city_name()),
				Field.Store.YES, Field.Index.NO);
		Field weixin_status = new Field(luceneVo.WEIXIN_STATUS,
				CommUtil.null2String(luceneVo.getVo_weixin_status()),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		Field goods_status = new Field(luceneVo.GOODS_STATUS,
				CommUtil.null2String(luceneVo.getVo_goods_status()),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		Field gcId = new Field(luceneVo.GC_ID,
				CommUtil.null2String(luceneVo.getVo_gc_id()),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		Field brandId = new Field(luceneVo.BRAND_ID,
				CommUtil.null2String(luceneVo.getVo_brand_id()),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		document.add(type);
		document.add(id);
		document.add(title);
		document.add(goods_serial);
		document.add(seo_keywords);
		document.add(content);
		document.add(goods_brand);
		document.add(brandId);
		document.add(add_time);
		document.add(goods_salenum);
		document.add(goods_sepc);
		document.add(store_name);
		document.add(store_ower);
		document.add(goods_inventory_detail);
		document.add(goods_promotion);
		document.add(goods_price);
		document.add(store_price);
		document.add(goods_current_price);
		document.add(city_id);
		document.add(city_name);
		document.add(evalutes_num);
		document.add(store_grade);
		document.add(description_evaluate);
		document.add(storeId);
		document.add(userId);
		document.add(goods_click);
		document.add(goods_collect);
		document.add(img_path);
		document.add(img_name);
		document.add(img_ext);
		document.add(inventory_type);
		document.add(goods_inventory);
		document.add(gcId);
		document.add(weixin_status);
		document.add(goods_status);
		Map<String,String> goods_specification =new HashMap<String,String>();
		Map<String,String> goods_type_property = new HashMap<String,String>();
		goods_specification=luceneVo.getVo_goods_specification();
		for (String key : goods_specification.keySet()) {
			   Field specification = new Field(key,
						CommUtil.null2String(goods_specification.get(key)),
						Field.Store.YES, Field.Index.NOT_ANALYZED);
			   document.add(specification);
		}
		goods_type_property=luceneVo.getVo_goods_type_property();
		for (String key : goods_type_property.keySet()) {
			String value=CommUtil.null2String(goods_type_property.get(key));
		    Field property = new Field(key,
				   value,
					Field.Store.YES, Field.Index.NOT_ANALYZED);
		    document.add(property);
		}
		return document;
	}

	/**
	 * 预处理商品
	 * 新增或更新lucence索引
	 * @author lihao
	 * @date 2015-07-09
	 * @param goods 商品
	 * @param type 索引类型 r.新增所有 u.更新索引
	 */
	public  void ruLuceneIndex(Goods goods,String type) {
		// lucene索引
		String goods_lucene_path = System.getProperty("user.dir")
				+ File.separator + "luence" + File.separator
				+ "goods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneVo vo = new LuceneVo();
		vo.setVo_id(goods.getId());
		vo.setVo_title(goods.getGoods_name()==null?"":goods.getGoods_name());
		vo.setVo_content(goods.getGoods_details()==null?"":goods.getGoods_details());
		vo.setVo_type("goods");
		vo.setVo_store_price(CommUtil.null2Double(goods
				.getStore_price()));
		vo.setVo_add_time(goods.getAddTime().getTime());
		vo.setVo_goods_salenum(goods.getGoods_salenum());
		vo.setVo_seo_keywords(goods.getSeo_keywords()==null?"":goods.getSeo_keywords());
		vo.setVo_goods_promotion(goods.getGoods_promotion());
		vo.setVo_goods_price(goods.getGoods_price().doubleValue());
		vo.setVo_goods_serial(goods.getGoods_serial());
		vo.setVo_goods_store(goods.getGoods_store().getStore_name());
		vo.setVo_goods_brand(goods.getGoods_brand()==null?"":goods.getGoods_brand().getName());
		vo.setVo_goods_sepc(goods.getGoods_property());//属性规格
		vo.setVo_goods_inventory_detail(goods.getGoods_inventory_detail());//规格库存
		vo.setVo_goods_current_price(goods.getGoods_current_price().doubleValue());
		vo.setVo_description_evaluate(goods.getDescription_evaluate());
		vo.setVo_store_id(goods.getGoods_store().getId()+"");
		vo.setVo_user_id(goods.getGoods_store().getUser().getId());
		vo.setVo_store_ower(goods.getGoods_store().getStore_ower());
		vo.setVo_goods_click(goods.getGoods_click());
		vo.setVo_goods_collect(goods.getGoods_collect());
		vo.setVo_img_path(goods.getGoods_main_photo()==null?"":goods.getGoods_main_photo().getPath());
		vo.setVo_img_name(goods.getGoods_main_photo()==null?"":goods.getGoods_main_photo().getName());
		vo.setVo_img_ext(goods.getGoods_main_photo()==null?"":goods.getGoods_main_photo().getExt());
		vo.setVo_city_id(goods.getGoods_store().getArea().getParent().getId());
		vo.setVo_evalutes_num(goods.getEvaluates()==null?0:goods.getEvaluates().size());
		vo.setVo_store_grade(goods.getGoods_store().getGrade().getGradeName());
		vo.setVo_inventory_type(goods.getInventory_type());
		vo.setVo_goods_inventory(goods.getGoods_inventory());
		vo.setVo_city_name(goods.getGoods_store().getArea().getParent().getAreaName());
		vo.setVo_weixin_status(goods.getGoods_store().getWeixin_status()+"");
		vo.setVo_gc_id(goods.getGc()==null?"":goods.getGc().getId()+"");
		vo.setVo_brand_id(goods.getGoods_brand()==null?null:goods.getGoods_brand().getId()+"");
		vo.setVo_goods_status(goods.getGoods_status()+"");
		List<GoodsSpecProperty> goods_specs = new ArrayList<GoodsSpecProperty>();
		goods_specs=goods.getGoods_specs();
		Map<String,String> goods_specification =new HashMap<String,String>();
		for(GoodsSpecProperty gs : goods_specs){
			goods_specification.put(LuceneVo.GOODS_SPECIFICATION+"_"+gs.getSpec().getId(),gs.getId()+"_"+gs.getValue());
		}
		vo.setVo_goods_specification(goods_specification);//商品规格
		Map<String,String> goods_type_property = new HashMap<String,String>();
		String goodsPropertys=goods.getGoods_property();
		if(goodsPropertys!=null && !"".equals(goodsPropertys)){
			JSONArray jsonArry = new JSONArray();
			jsonArry=jsonArry.parseArray(goodsPropertys);
			for (int i = 0; i < jsonArry.size(); i++) {
				JSONObject json = (JSONObject) jsonArry.get(i);
				goods_type_property.put(LuceneVo.GOODS_TYPE_PROPERTY+"_"+json.get("id"),json.get("id")+"_"+json.getString("val").trim());
			}
		}
		vo.setVo_goods_type_property(goods_type_property);//商品属性值
		if("u".equals(type)){
			this.setIndex_path(goods_lucene_path);
			this.update(CommUtil.null2String(goods.getId()), vo);
		}else if("r".equals(type)){
			this.setIndex_path(goods_lucene_path);
			this.writeIndex(vo);
		}
	}
	
	/**
	 * 删除商品索引
	 * @param id 商品ID
	 */
	public void removeLuceneIndex(String id){
		String goods_lucene_path = System.getProperty("user.dir")
		+ File.separator + "luence" + File.separator + "goods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(goods_lucene_path);
		lucene.delete_index(CommUtil.null2String(id));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LuceneUtil lucence = LuceneUtil.instance();
		lucence.setIndex_path("E:\\apache-tomcat-7.0.42\\luence\\goods");
		Date d1 = new Date();
		LuceneResult list = lucence.search("专柜正品黑色时尚冬装男", 0,20, 0, 500.0, null, null,null,null,null,null,null);
		Date d2 = new Date();
		System.out.println("查询时间为：" + (d2.getTime() - d1.getTime()) + "毫秒");
		for (int i = 0; i < list.getVo_list().size(); i++) {
			LuceneVo vo = list.getVo_list().get(i);
			System.out.println("标题：" + vo.getVo_title());
			System.out.println("价格:" + vo.getVo_store_price());
			System.out.println("添加时间:" + vo.getVo_add_time());
		}
		System.out.println("查询结果为:" + list.getVo_list().size());

	}
}
