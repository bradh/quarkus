package io.quarkus.grpc.client;

import static org.assertj.core.api.Assertions.assertThat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloReplyOrBuilder;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.HelloRequestOrBuilder;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.server.services.HelloService;
import io.quarkus.test.QuarkusUnitTest;

public class BlockingStubInjectionTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(MyConsumer.class, GreeterGrpc.class, GreeterGrpc.GreeterBlockingStub.class,
                            HelloService.class, HelloRequest.class, HelloReply.class,
                            HelloReplyOrBuilder.class, HelloRequestOrBuilder.class))
            .withConfigurationResource("hello-config.properties");

    @Inject
    MyConsumer service;

    @Test
    public void test() {
        String neo = service.invoke("neo");
        assertThat(neo).isEqualTo("Hello neo");

        neo = service.invokeWeird("neo");
        assertThat(neo).isEqualTo("Hello neo");
    }

    @ApplicationScoped
    static class MyConsumer {

        @GrpcClient("hello-service")
        GreeterGrpc.GreeterBlockingStub service;

        public String invoke(String s) {
            return service.sayHello(HelloRequest.newBuilder().setName(s).build())
                    .getMessage();
        }

        public String invokeWeird(String s) {
            return service.wEIRD(HelloRequest.newBuilder().setName(s).build())
                    .getMessage();
        }

    }
}
