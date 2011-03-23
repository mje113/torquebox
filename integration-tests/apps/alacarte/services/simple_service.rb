
class SimpleService 
  include TorqueBox::Injectors

  def initialize(opts={})
    puts "init"
    @webserver = mc('jboss.web:service=WebServer')
    @something = cdi( org.torquebox.ThingOne )
    @logger = log( "Foobledydoo" )
  end

  def start() 
    puts "start"
    @should_run = true
    spawn_thread()
  end

  def spawn_thread()
    puts "spawn_thread"
    @thread = Thread.new do
      while @should_run 
        loop_once
        sleep( 1 )
      end
    end
  end

  def loop_once
    @logger.info "JC: looping once..."
    if ( @webserver && @something )
      basedir = ENV['BASEDIR' ]
      basedir.gsub!( %r(\\:), ':' )
      basedir.gsub!( %r(\\\\), '\\' )
      touchfile = File.join( basedir, 'target', 'touchfile.txt' )
      File.open( touchfile, 'w' ) do |f|
        f.puts( "Updated #{Time.now}" )
      end
    end
  end

  def stop()
    @should_run = false
    @thread.join
  end

end
