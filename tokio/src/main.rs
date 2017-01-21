extern crate futures;
extern crate num_cpus;
extern crate serde;
#[macro_use]
extern crate serde_derive;
extern crate serde_json;
extern crate tokio_minihttp;
extern crate tokio_proto;
extern crate tokio_service;

use futures::future;
use tokio_minihttp::{Request, Response, Http};
use tokio_proto::TcpServer;
use tokio_service::Service;

#[derive(Serialize, Deserialize, Debug)]
struct Tweet {
    id: u64,
    author: String,
    content: String,
}

struct TweetService {
    counter: u64,
}

impl TweetService {
    pub fn new() -> TweetService {
        TweetService {
            counter: 1000000,
        }
    }

    pub fn list(&self) -> Vec<Tweet> {
        //self.counter += 1;
        vec!(
            Tweet {
                id: self.counter,
                author: "author1".to_string(),
                content: "Hello, World!".to_string(),
            }
        )
    }
}

struct TweetApi {
    tweet_service: TweetService,
}

impl TweetApi {
    pub fn new(tweet_service: TweetService) -> TweetApi {
        TweetApi {
            tweet_service: tweet_service,
        }
    }
}

impl Service for TweetApi {
    type Request = Request;
    type Response = Response;
    type Error = std::io::Error;
    type Future = future::Ok<Response, std::io::Error>;

    fn call(&self, req: Request) -> Self::Future {
        let mut resp = Response::new();
        match req.path() {
            "/v1/tweets" => {
                let tweets = self.tweet_service.list();
                let json = serde_json::to_string(&tweets).unwrap();
                resp.header("Content-Type", "application/json")
                    .body(&json);
            }
            _ => {
                resp.status_code(404, "Not Found");
            }
        }
        future::ok(resp)
    }
}

fn main() {
    let address = "0.0.0.0:8080".parse().unwrap();
    let mut server = TcpServer::new(Http, address);
    server.threads(num_cpus::get());
    server.serve(|| {
        let tweet_service = TweetService::new();
        let api = TweetApi::new(tweet_service);
        Ok(api)
    })
}
