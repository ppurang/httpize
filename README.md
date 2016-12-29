# HTTP-IZE 

http://httpize.herokuapp.com/

Resemble HTTP as in http://www.merriam-webster.com/dictionary/-ize

# Run 

You can use your own `sbt` set-up or just run the `ppsbt` to start with 0 dependencies. Things should just work. In the REPL: 

```sh
sbt> reStart  
[info] Application httpize not yet started
[info] Starting application httpize in the background ...
httpize Starting org.purang.net.httpize.Httpize.main()
[success] Total time: 1 s, completed Sep 21, 2014 3:04:18 PM
httpize Starting Http4s-blaze example on '0.0.0.0:8080'
```

Use your browser to hit [http://localhost:8080/](http://localhost:8080/).

For rapid development use triggered `reStart`.  

```sh
sbt> ~reStart
```

To deploy the project to a vm at heroku (and given that heroku toolbelt is already installed):

```sh 
shell> git push heroku master   #this isn't the sbt REPL but the shell
```

Happy hacking!


# Collaboration
Fork the repository and create pull requests.
    
# Inspirations

httpbin.org for the inspiration

http4s_demo for seeding the project 
