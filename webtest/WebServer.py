import html
from http import server
import argparse
import os
from http.server import BaseHTTPRequestHandler, urllib, SimpleHTTPRequestHandler
import sys
import posixpath
import mimetypes
import io

class BaseMDHandler(SimpleHTTPRequestHandler):

    def guess_type(self, path):
        """Guess the type of a file.

        Argument is a PATH (a filename).

        Return value is a string of the form type/subtype,
        usable for a MIME Content-type header.

        The default implementation looks the file's extension
        up in the table self.extensions_map, using application/octet-stream
        as a default; however it would be permissible (if
        slow) to look inside the data to make a better guess.

        """

        base, ext = posixpath.splitext(path)
        if ext in self.extensions_map:
            return self.extensions_map[ext]
        ext = ext.lower()
        if ext in self.extensions_map:
            return self.extensions_map[ext]
        else:
            return self.extensions_map['']

    if not mimetypes.inited:
        mimetypes.init()  # try to read system mime.types
    extensions_map = mimetypes.types_map.copy()
    extensions_map.update({
        '': 'application/octet-stream',  # Default
        '.py': 'text/plain',
        '.c': 'text/plain',
        '.h': 'text/plain',
        '.json': 'application/json',
    })

    forceplain = False

    def do_PUT(self):
        contents = self.rfile.
    def send_head(self):
        """Common code for GET and HEAD commands.

            This sends the response code and MIME headers.

            Return value is either a file object (which has to be copied
            to the outputfile by the caller unless the command was HEAD,
            and must be closed by the caller under all circumstances), or
            None, in which case the caller has nothing further to do.

            """
        path = self.translate_path(self.path)
        f = None
        if os.path.isdir(path):
            if not self.path.endswith('/'):
                # redirect browser - doing basically what apache does
                self.send_response(301)
                self.send_header("Location", self.path + "/")
                self.end_headers()
                return None
            for index in "index.html", "index.htm", "app.html", "app.htm":
                index = os.path.join(path, index)
                if os.path.exists(index):
                    path = index
                    break
            else:
                return self.list_directory(path)
        ctype = None
        print("send_head: " + str(self.forceplain))
        if self.forceplain: ctype = self.extensions_map['']
        else: ctype = self.guess_type(path)

        try:
            f = open(path, 'rb')
        except OSError:
            self.send_error(404, "File not found")
            return None
        try:
            self.send_response(200)
            self.send_header("Content-type", ctype)
            self.log_message("MimeType: %s", ctype)
            fs = os.fstat(f.fileno())
            self.send_header("Content-Length", str(fs[6]))
            self.send_header("Last-Modified", self.date_time_string(fs.st_mtime))
            self.end_headers()
            return f
        except:
            f.close()
            raise

    def translate_path(self, path):
        """Translate a /-separated PATH to the local filename syntax.

        Components that mean special things to the local file system
        (e.g. drive or directory names) are ignored.  (XXX They should
        probably be diagnosed.)

        """

        # abandon query parameters
        path = path.split('?', 1)[0]
        path = path.split('#', 1)[0]
        # Don't forget explicit trailing slash when normalizing. Issue17324
        trailing_slash = path.rstrip().endswith('/')
        self.forceplain = False
        def grabOSPath(arguments):
            osPath = os.getcwd()
            for word in arguments:
                drive, word = os.path.splitdrive(word)
                head, word = os.path.split(word)
                if word in (os.curdir, os.pardir): continue
                osPath = os.path.join(osPath, word)
            if trailing_slash:
                osPath += '/'
            return osPath

        path = posixpath.normpath(urllib.parse.unquote(path))
        if path.startswith('/static'):
            path = path[7:]
        if path.startswith('/api') and not path.endswith('.json'):
            path += '.json'
        if path.startswith('/plugin') and not path.startswith('/plugin/'):
            path = '/api'
        elif path.startswith('/plugin/'):
            path = '/api/' + path[8:]
            self.forceplain = True
        # if path is None or path == '/' or path == '':
        # path = self.translate_path('/app.html')
        words = path.split('/')

        print("Translate: " + str(self.forceplain))

        words = filter(None, words)
        npath = grabOSPath(words)
        if (not os.path.exists(npath)) and __debug__:
            print("Path does not exist\n\tNewPath: " + npath + "\n\tPath: " + path)

        return npath

    def list_directory(self, path):
        """Helper to produce a directory listing (absent index.html).

        Return value is either a file object, or None (indicating an
        error).  In either case, the headers are sent, making the
        interface the same as for send_head().

        """
        try:
            list = os.listdir(path)
        except OSError:
            self.send_error(404, "No permission to list directory")
            return None
        list.sort(key=lambda a: a.lower())
        r = []
        displaypath = html.escape(urllib.parse.unquote(self.path))
        enc = sys.getfilesystemencoding()
        mime = None
        if not 'api' in path:
            mime = 'text/html'
            title = 'Directory listing for %s' % displaypath
            r.append('<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" '
                     '"http://www.w3.org/TR/html4/strict.dtd">')
            r.append('<html>\n<head>')
            r.append('<meta http-equiv="Content-Type" '
                     'content="text/html; charset=%s">' % enc)
            r.append('<title>%s</title>\n</head>' % title)
            r.append('<body>\n<h1>%s</h1>' % title)
            r.append('<hr>\n<ul>')
            for name in list:
                fullname = os.path.join(path, name)
                displayname = linkname = name
                # Append / for directories or @ for symbolic links
                if os.path.isdir(fullname):
                    displayname = name + "/"
                    linkname = name + "/"
                if os.path.islink(fullname):
                    displayname = name + "@"
                    # Note: a link to a directory displays with @ and links with /
                r.append('<li><a href="%s">%s</a></li>'
                         % (urllib.parse.quote(linkname), html.escape(displayname)))
                r.append('</ul>\n<hr>\n</body>\n</html>\n')
        else:
            mime = "text/plain"
            for name in list:
                fullname = os.path.join(path, name)
                displayname = linkname = name
                # Append / for directories or @ for symbolic links
                if os.path.isdir(fullname):
                    linkname = name + "/"
                if os.path.islink(fullname):
                    displayname = name + "@"
                    # Note: a link to a directory displays with @ and links with /
                r.append(linkname)
        encoded = '\n'.join(r).encode(enc)
        f = io.BytesIO()
        f.write(encoded)
        f.seek(0)
        self.send_response(200)
        self.send_header("Content-type", mime + "; charset=%s" % enc)
        self.send_header("Content-Length", str(len(encoded)))
        self.end_headers()
        return f


def patch(HandlerClass):
    return type('MDHandler', (BaseMDHandler, HandlerClass), dict())


class MDServer(server.HTTPServer):
    pass


def test(HandlerClass=BaseHTTPRequestHandler,
         protocol="HTTP/1.0", port=8000, bind=""):
    """Test the HTTP request handler class.

    This runs an HTTP server on port 8000 (or the first command line
    argument).

    """
    server_address = (bind, port)

    HandlerClass.protocol_version = protocol

    HandlerClass = patch(HandlerClass)
    httpd = MDServer(server_address, HandlerClass)

    sa = httpd.socket.getsockname()
    print("Working directory: " + os.getcwd())
    print("Serving HTTP on", sa[0], "port", sa[1], "...")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nKeyboard interrupt received, exiting.")
        httpd.server_close()
        sys.exit(0)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--cgi', action='store_true',
                        help='Run as CGI Server')
    parser.add_argument('--bind', '-b', default='localhost', metavar='ADDRESS',
                        help='Specify alternate bind address '
                             '[default: all interfaces]')
    parser.add_argument('port', action='store',
                        default=8000, type=int,
                        nargs='?',
                        help='Specify alternate port [default: 8000]')

    parser.add_argument('location', action='store',
                        default=(os.pardir + os.altsep + 'web'),
                        nargs='*',
                        help='Specify a server location to run. [default: Current directory]')

    args = parser.parse_args()
    if args.cgi:
        handler_class = server.CGIHTTPRequestHandler
    else:
        handler_class = server.SimpleHTTPRequestHandler

    if os.path.isdir(args.location):
        os.chdir(args.location)
    test(HandlerClass=handler_class, port=args.port, bind=args.bind)