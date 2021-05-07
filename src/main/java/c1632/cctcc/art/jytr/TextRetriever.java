/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1632.cctcc.art.jytr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Jonathan Chang, Chun-yien <ccy@musicapoetica.org>
 */
public class TextRetriever {

  public static void main(String[] args) throws MalformedURLException, URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {

    var yi_tian = "https://www.51shucheng.net/zh-tw/wuxia/yitiantulongji";
    var shen_diao = "https://www.51shucheng.net/zh-tw/wuxia/shendiaoxialv";
    var she_diao = "https://www.51shucheng.net/zh-tw/wuxia/shediaoyingxiongzhuan";

    for (String url : new String[]{yi_tian, shen_diao, she_diao}) {
      var doc = Jsoup.connect(url).get();
      var title = doc.getElementsByTag("h1").text();
      System.out.println(title);
      
      var folder = Path.of(System.getProperty("user.dir"), "downloaded", title);
      folder.toFile().mkdirs();
      
      var counter = 0;
      for (var li : doc.select("li")) {
        var doc1 = Jsoup.connect(li.getElementsByTag("a").attr("href")).get();
        var title1 = String.format("%02d. %s", ++counter, doc1.getElementsByTag("h1").text());
        System.out.println(title1);
      
        var content = doc1.getElementById("neirong").getElementsByTag("p").stream()
                .map(Element::text)
                .collect(Collectors.toList());
        Files.write(folder.resolve(title1 + ".txt"), content);
      }
    }
  }
}
