import { Application, Router } from "https://raw.githubusercontent.com/oakserver/oak/main/mod.ts";
import { delay } from  "https://deno.land/std@0.92.0/async/delay.ts";

interface Tweet {
  id: number;
  author: string;
  content: string;
}

class TweetService {
  counter: number;

  constructor() {
    this.counter = 1000000;
  }

  async list(): Promise<Array<Tweet>> {
    await delay(16)
    this.counter += 1;
    return [
      {
        "id": this.counter,
        "author": "author1",
        "content": "Hello, World!",
      }
    ];
  }
}

const tweetService = new TweetService();

const router = new Router();
router
  .get("/v1/tweets", async (context) => {
    context.response.body = await tweetService.list();
  });

const app = new Application();
app.use(router.routes());
app.use(router.allowedMethods());

await app.listen({ port: 8080 });
