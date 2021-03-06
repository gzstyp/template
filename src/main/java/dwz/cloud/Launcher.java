package dwz.cloud;

import dwz.cloud.tool.ToolClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashSet;
import java.util.Set;

public class Launcher extends AbstractVerticle {

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    //创建HttpServer
    final HttpServer server = vertx.createHttpServer();

    //第二步,初始化|实例化 Router,若要添加跨域请求的话,随着就配置跨域
    final Router router = Router.router(vertx);
    final Set<HttpMethod> methods = new HashSet<>();
    methods.add(HttpMethod.OPTIONS);
    methods.add(HttpMethod.GET);
    methods.add(HttpMethod.POST);
    router.route().blockingHandler(CorsHandler.create("http://192.168.3.108").allowCredentials(true).allowedHeader("content-type").maxAgeSeconds(86400).allowedMethods(methods));

    //第三步,配置Router解析url
    router.route("/").blockingHandler(context -> {
      ToolClient.responseJson(context,ToolClient.createJson(200,"操作成功"));
    });

    router.route("/*").handler(StaticHandler.create()); //配置静态资源访问,其访问路径/favicon.ico,若前面配置的 /static/*,则访问是 /static/favicon.ico

    //第四步,将router和 HttpServer 绑定[若是使用配置文件则这样实例化,如果不配置文件则把它挪动到lambda外边即可]
    server.requestHandler(router).listen(89,http -> {
      if (http.succeeded()){
        startPromise.complete();
        System.out.println("---应用启动成功---");
      } else {
        //startPromise.fail(http.cause());
        System.out.println("---Launcher应用启动失败---"+http.cause());
      }
    });
  }
}