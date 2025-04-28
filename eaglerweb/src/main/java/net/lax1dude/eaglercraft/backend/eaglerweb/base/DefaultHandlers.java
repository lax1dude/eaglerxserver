/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;

import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;

class DefaultHandlers {

	private static final String AUTOINDEX_CSS = "<style type=\"text/css\">" + "th { text-align: left; } "
			+ ".ar { text-align: right; }"
			+ ".icn-none { display: inline-block; width: 1em; height: 1em; margin:2px; vertical-align: middle; }"
			+ ".icn-folder { display: inline-block; width: 1em; height: 1em; margin:2px; background: no-repeat center / contain url(\"data:image/gif;base64,R0lGODlhFAAWAMIAAP/////Mmcz//5lmMzMzMwAAAAAAAAAAACH+TlRoaXMgYXJ0IGlzIGluIHRoZSBwdWJsaWMgZG9tYWluLiBLZXZpbiBIdWdoZXMsIGtldmluaEBlaXQuY29tLCBTZXB0ZW1iZXIgMTk5NQAh+QQBAAACACwAAAAAFAAWAAADVCi63P4wyklZufjOErrvRcR9ZKYpxUB6aokGQyzHKxyO9RoTV54PPJyPBewNSUXhcWc8soJOIjTaSVJhVphWxd3CeILUbDwmgMPmtHrNIyxM8Iw7AQA7\"); vertical-align: middle; }"
			+ ".icn-file { display: inline-block; width: 1em; height: 1em; margin:2px; background: no-repeat center / contain url(\"data:image/gif;base64,R0lGODlhFAAWAMIAAP///8z//5mZmTMzMwAAAAAAAAAAAAAAACH+TlRoaXMgYXJ0IGlzIGluIHRoZSBwdWJsaWMgZG9tYWluLiBLZXZpbiBIdWdoZXMsIGtldmluaEBlaXQuY29tLCBTZXB0ZW1iZXIgMTk5NQAh+QQBAAABACwAAAAAFAAWAAADWDi6vPEwDECrnSO+aTvPEddVIriN1wVxROtSxBDPJwq7bo23luALhJqt8gtKbrsXBSgcEo2spBLAPDp7UKT02bxWRdrp94rtbpdZMrrr/A5+8LhPFpHajQkAOw==\"); vertical-align: middle; }"
			+ "</style>";

	private final String brandString;
	private final String serverName;
	private final boolean enableCORS;
	private final byte[] default429;
	private final byte[] default500;

	DefaultHandlers(EaglerWeb<?> eaglerWeb) {
		this.brandString = eaglerWeb.getPlatform().getVersionString().replace("<", "&lt;").replace(">", "&gt;");
		this.serverName = eaglerWeb.getServer().getServerName().replace("<", "&lt;").replace(">", "&gt;");
		this.enableCORS = eaglerWeb.getConfig().getEnableCORS();
		StringBuilder pageBuilder = new StringBuilder(512);
		pageBuilder.append("<!DOCTYPE html><html><head><title>");
		pageBuilder.append(serverName);
		pageBuilder.append(
				"</title></head><body style=\"font-family:sans-serif;text-align:center;\"><h1>429 Too Many Requests</h1><hr>");
		pageBuilder.append("<p style=\"font-size:1.2em;\">Try again later</p><p>");
		pageBuilder.append(brandString);
		pageBuilder.append("</p></body></html>");
		this.default429 = pageBuilder.toString().getBytes(StandardCharsets.UTF_8);
		pageBuilder = new StringBuilder(512);
		pageBuilder.append("<!DOCTYPE html><html><head><title>");
		pageBuilder.append(serverName);
		pageBuilder.append(
				"</title></head><body style=\"font-family:sans-serif;text-align:center;\"><h1>500 Internal Error</h1><hr>");
		pageBuilder.append("<p style=\"font-size:1.2em;\">Contact the server owner</p><p>");
		pageBuilder.append(brandString);
		pageBuilder.append("</p></body></html>");
		this.default500 = pageBuilder.toString().getBytes(StandardCharsets.UTF_8);
	}

	protected static void htmlEntities(CharSequence input, StringBuilder result) {
		for (int i = 0, l = input.length(); i < l; ++i) {
			char c = input.charAt(i);
			switch (c) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '\"':
				result.append("&quot;");
				break;
			case '&':
				result.append("&amp;");
				break;
			default:
				result.append(c);
				break;
			}
		}
	}

	void handleAutoIndex(IRequestContext ctx, DateFormat dateFormat, IndexNodeFolder dir) {
		String dirStr = ctx.getPath();
		StringBuilder pageBuilder = new StringBuilder(4096);
		pageBuilder.append("<!DOCTYPE html><html><head><title>Index Of: ");
		htmlEntities(dirStr, pageBuilder);
		pageBuilder.append(" - ");
		pageBuilder.append(serverName);
		pageBuilder.append("</title>");
		pageBuilder.append(AUTOINDEX_CSS);
		pageBuilder.append(
				"</head><body style=\"font-family:sans-serif;\"><p style=\"line-height:1.75em;\"><span style=\"font-size:2em;\">Index Of: <span style=\"font-weight:bold;\">");
		htmlEntities(dirStr, pageBuilder);
		pageBuilder.append("</span></span><br>&nbsp;");
		int totalFile = 0, totalFolder = 0;
		for (IndexNode idx : dir) {
			if (idx.isDirectory()) {
				++totalFolder;
			} else {
				++totalFile;
			}
		}
		if (totalFolder > 0) {
			pageBuilder.append(totalFolder);
			pageBuilder.append(totalFolder == 1 ? " folder, " : " folders, ");
		}
		pageBuilder.append(totalFile);
		pageBuilder.append(totalFile == 1 ? " file" : " files");
		pageBuilder.append(" total.</p><hr><table style=\"font-size:1.2em;\">");
		pageBuilder.append(
				"<thead><tr><tr><th><span class=\"icn-none\"></span></th><th>Name</th><th>&ensp;Last Modified</th><th class=\"ar\">&ensp;Size</th></tr></thead><tbody>");
		IndexNode parent = dir.getParent();
		if (parent != null) {
			pageBuilder.append(
					"<tr><td><span class=\"icn-folder\"></span></td><td><a href=\"../\">../</a></td><td>&ensp;");
			pageBuilder.append(formatDate(dateFormat, parent.lastModified()));
			pageBuilder.append("</td><td class=\"ar\">&ensp;-</td></tr>");
		}
		for (IndexNode idx : dir) {
			if (idx.isDirectory()) {
				String name = idx.getName();
				pageBuilder.append("<tr><td><span class=\"icn-folder\"></span></td><td><a href=\"");
				htmlEntities(name, pageBuilder);
				pageBuilder.append("/\">");
				htmlEntities(name, pageBuilder);
				pageBuilder.append("/</a></td><td>&ensp;");
				pageBuilder.append(formatDate(dateFormat, idx.lastModified()));
				pageBuilder.append("</td><td class=\"ar\">&ensp;-</td></tr>");
			}
		}
		for (IndexNode idx : dir) {
			if (!idx.isDirectory()) {
				String name = idx.getName();
				pageBuilder.append("<tr><td><span class=\"icn-file\"></span></td><td><a href=\"");
				htmlEntities(name, pageBuilder);
				pageBuilder.append("/\">");
				htmlEntities(name, pageBuilder);
				pageBuilder.append("</a></td><td>&ensp;");
				pageBuilder.append(formatDate(dateFormat, idx.lastModified()));
				pageBuilder.append("</td><td class=\"ar\">&ensp;");
				formatSize(idx.getSize(), pageBuilder);
				pageBuilder.append("</td></tr>");
			}
		}
		pageBuilder.append("</tbody></table><hr><p style=\"font-style:italic;\">");
		pageBuilder.append(brandString);
		pageBuilder.append("</p></body></html>");
		ctx.setResponseCode(200);
		ctx.setResponseBody(pageBuilder, StandardCharsets.UTF_8);
		ctx.addResponseHeader("content-type", "text/html; charset=utf-8");
		if (enableCORS) {
			ctx.addResponseHeader("access-control-allow-origin", "*");
		}
	}

	private static String formatDate(DateFormat dateFormat, Date date) {
		return dateFormat != null ? dateFormat.format(date) : date.toString();
	}

	private void formatSize(long size, StringBuilder pageBuilder) {
		if (size > 1024l * 1024l * 1024l) {
			pageBuilder.append(String.format("%.02f", (size / (1024l * 1024l)) / 1024.0));
			pageBuilder.append(" GiB");
		} else if (size > 1024l * 1024l) {
			pageBuilder.append(String.format("%.02f", (size / 1024l) / 1024.0));
			pageBuilder.append(" MiB");
		} else if (size > 1024l) {
			pageBuilder.append(String.format("%.02f", size / 1024.0));
			pageBuilder.append(" KiB");
		} else {
			pageBuilder.append(size);
			pageBuilder.append(" B");
		}
	}

	void handle404(IRequestContext ctx) {
		StringBuilder page404Builder = new StringBuilder(512);
		page404Builder.append("<!DOCTYPE html><html><head><title>");
		page404Builder.append(serverName);
		page404Builder.append(
				"</title></head><body style=\"font-family:sans-serif;text-align:center;\"><h1>404 Not Found</h1><hr>");
		page404Builder.append(
				"<p style=\"font-size:1.2em;\">The requested resource <span id=\"addr\" style=\"font-family:monospace;font-weight:bold;background-color:#EEEEEE;padding:3px 4px;\">");
		htmlEntities(ctx.getPath(), page404Builder);
		page404Builder.append("</span> could not be found on this server</p><p>");
		page404Builder.append(brandString);
		page404Builder.append("</p></body></html>");
		ctx.setResponseCode(404);
		ctx.setResponseBody(page404Builder, StandardCharsets.UTF_8);
		ctx.addResponseHeader("content-type", "text/html; charset=utf-8");
		if (enableCORS) {
			ctx.addResponseHeader("access-control-allow-origin", "*");
		}
	}

	void handle429(IRequestContext ctx) {
		ctx.setResponseCode(429);
		ctx.setResponseBody(default429);
		ctx.addResponseHeader("content-type", "text/html; charset=utf-8");
		if (enableCORS) {
			ctx.addResponseHeader("access-control-allow-origin", "*");
		}
	}

	void handle500(IRequestContext ctx) {
		ctx.setResponseCode(500);
		ctx.setResponseBody(default500);
		ctx.addResponseHeader("content-type", "text/html; charset=utf-8");
		if (enableCORS) {
			ctx.addResponseHeader("access-control-allow-origin", "*");
		}
	}

}
